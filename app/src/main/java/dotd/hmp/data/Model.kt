package dotd.hmp.data

import android.util.Log
import androidx.room.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import dotd.hmp.MyApplication
import dotd.hmp.R
import dotd.hmp.hepler.*
import java.io.File
import java.io.Serializable
import java.lang.Exception
import java.util.*
import kotlin.collections.HashSet


/*
jsonData:
[
   {        <====== This is Record
       "id": {     <====== This is Field
            "fieldType": "TEXT",
            "value": "abZhdhDZMWlamKzmeDZZ"
       },
       "createTime": {
            "fieldType": "DATETIME",
            "value": "12031200222"
       },
       "name": {
            "fieldType": "TEXT",
            "value": "Peter"
        },
        "age": {
            "fieldType": "NUMBER",
            "value": "20"
        }
    },
]
All field value store as String,
Not use org.json.JSONObject
 */

@Entity()
class Model: Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var name: String = ""
    var icon: Int? = null
    var jsonFields: String = ""
    var sequence: Int = 0
    var description: String = ""

    // not store jsonData to Database. save to file.
    @Ignore
    var jsonData = "[]"


    @Ignore
    var isSelected = false

    constructor()

    @Ignore
    constructor(name: String, icon: Int) {
        this.name = name
        this.icon = icon
    }

    companion object {
        val itemAddNewModel by lazy {
                Model(getStr(R.string.add), R.drawable.ic_baseline_add_24).apply {
                    description = getStr(R.string.add_new_model)
            }
        }
    }
    fun isItemAddNewModel(): Boolean = this == itemAddNewModel


    fun setFieldList(list: MutableList<Field>) {
        list.forEach { field ->
            if (field.fieldName.isDefaultField()) {
                var defaultFieldName = ""
                defaultField.forEach { defaultFieldName += "$it, " }
                throw Exception("Field name mustn't be the same as default field name: (${field.fieldName}), default field: [$defaultFieldName]")
            }
        }
        this.jsonFields = Gson().toJson(list)
    }

    // field list not contain default field
    fun getFieldList(): List<Field> = Gson().fromJson(this.jsonFields, Array<Field>::class.java).asList()

    fun getField(fieldName: String): Field? {
        getFieldList().forEach {
            if (fieldName == it.fieldName)
                return it
        }
        return null
    }

    fun addRecord(record: JsonObject) {
        if (!isRecordValidate(record)) {
            throw Exception("Exception when add new record, jsonOject not enough field when compared to fieldList of Model")
        }
        insertDefaultField(record)
        val recordList = getRecordList()
        recordList.add(record)
        jsonData = recordList.toString()
    }

    fun deleteRecord(record: JsonObject) {
        val id = record.getValueOfField("id")
        val recordList = getRecordList()
        for (r in recordList) {
            val id2 = r.getValueOfField("id")
            if (id == id2) {
                recordList.remove(r)
                jsonData = recordList.toString()
                break
            }
        }
    }

    fun updateRecord(record: JsonObject) {
        if (!isRecordValidate(record)) {
            throw Exception("Exception when update new record, jsonOject not enough field when compared to fieldList of Model")
        }

        val id = record.getValueOfField("id")
        val recordList = getRecordList()

        for ((i, v) in recordList.withIndex()) {
            val id2 = v.getValueOfField("id")
            if (id == id2) {
                insertUpdatetime(record)
                recordList.set(i, record)

                jsonData = recordList.toString()
                break
            }
        }
    }

    fun deleteAllRecord() {
        jsonData = "[]"
    }

    fun getRecordList(): MutableList<JsonObject> {
        if (jsonData == "[]") {
            jsonData = try {
                readFileAsTextUsingInputStream(getFilePath())
            } catch (e: Exception) {
                jsonData
            }
        }
        if (jsonData == "empty")
            jsonData = "[]"

        return Gson().fromJson(jsonData, Array<JsonObject>::class.java).asList().toMutableList()
    }

    fun setRecordList(list: List<JsonObject>) {
        this.jsonData = Gson().toJson(list)
    }

    fun isRecordValidate(record: JsonObject): Boolean {
        getFieldList().forEach {
            // record does not contain field defined in model
            if (!record.has(it.fieldName)) {
                return false
                // not check field has [fieldType, value] because performance
            }
        }
        return true
    }

    fun sortByField(field: Field, recordList: List<JsonObject> = getRecordList()): Model {
        if (recordList.isEmpty()) return this
        if (!hasField(field.fieldName)) {
            Log.e("Model", "Error at: [Model.kt, sortByField()]: Can't sort records, " +
                      "field name: \"$field.fieldName\" doesn't not exist.")
            return this
        }
        if (!field.isFieldCanSorted()) {
            Log.e("Model", "Error at: [Model.kt, sortByField()]: $field is not field type can sort.")
            return this
        }

        val model = this.clone()
        val listSorted: List<JsonObject> = when (field.fieldType) {
            FieldType.NUMBER -> {
                recordList.sortedWith(compareBy(
                    {it.getValueOfField(field).toFloat()},
                    {it.getValueOfField(field).toFloat()}
                ))
            }
            FieldType.TEXT, FieldType.DATETIME -> {
                recordList.sortedWith(compareBy(
                    {it.getValueOfField(field)},
                    {it.getValueOfField(field)}
                ))
            }
        }

        model.jsonData = listSorted.toString()
        return model
    }

    /**
     *
     * @param _withField:
     *   _withField = null => search value in all field
     *
     * @param searchMethod:
     *     relative: relative search
     *     exact: exact search
     *
     * @param records: search in records
     */
    fun searchRecords(
        keySearch: String,
        _withField: MutableList<Field>? = null,
        limit: Int? = null,
        searchMethod: String = "relative",
        records: List<JsonObject> = getRecordList(),
        filter: FilterRecord? = null
    ): Model {
        if (keySearch.trim() == "") return this

        var withField: MutableList<Field> = mutableListOf()

        when {
            filter != null -> {
                withField = mutableListOf(filter.getField())
            }
            _withField == null -> {
                getFieldList().forEach {
                    withField.add(it)
                }
                withField.add(Field(getStr(R.string.default_field_create_time), FieldType.DATETIME))
            }
            else -> {
                withField = _withField
            }
        }

        val model = this.clone()
        val resultSet = HashSet<JsonObject>()
        val key = keySearch.removeVietnameseAccents().toLowerCase(Locale.ROOT).trim()

        for (r in records) {
            for (field in withField) {
                val v = r.getValueOfField(field.fieldName)
                val value: String? = when(field.fieldType) {
                    FieldType.DATETIME ->
                        DateTimeHelper.timestampToDatetimeString(v.toLong()).toLowerCase(Locale.ROOT)

                    FieldType.TEXT, FieldType.NUMBER ->
                        v.removeVietnameseAccents().toLowerCase(Locale.ROOT).trim()

                    // field type not support for search
                    else -> null
                }

                value?.let {
                    if (filter != null) {
                        when (filter.operator) {
                            ">" ->
                            {
                                if (field.fieldType == FieldType.NUMBER) {
                                    if (value.toFloat() > key.toFloat())
                                        resultSet.add(r)
                                } else if (value > key) {
                                    resultSet.add(r)
                                }
                            }
                            ">=" ->
                            {
                                if (field.fieldType == FieldType.NUMBER) {
                                    if (value.toFloat() >= key.toFloat())
                                        resultSet.add(r)
                                } else if (value >= key) {
                                    resultSet.add(r)
                                }
                            }

                            "<" ->
                            {
                                if (field.fieldType == FieldType.NUMBER) {
                                    if (value.toFloat() < key.toFloat())
                                        resultSet.add(r)
                                } else if (value < key) {
                                    resultSet.add(r)
                                }
                            }
                            "<=" ->
                            {
                                if (field.fieldType == FieldType.NUMBER) {
                                    if (value.toFloat() <= key.toFloat())
                                        resultSet.add(r)
                                } else if (value <= key) {
                                    resultSet.add(r)
                                }
                            }
                            "=" ->
                                if (value == key)
                                    resultSet.add(r)
                            "!=" ->
                                if (value != key)
                                    resultSet.add(r)
                            "contains" ->
                                if (value.contains(key))
                                    resultSet.add(r)
                            "not contains" ->
                                if (!value.contains(key))
                                    resultSet.add(r)
                        }
                    } else {
                        when (searchMethod) {
                            "relative" ->
                                if (value.contains(key))
                                    resultSet.add(r)
                            "exact" ->
                                if (value == key)
                                    resultSet.add(r)
                        }
                    }
                }

            }
            if (limit != null && limit == resultSet.size)
                break
        }
        if (resultSet.size == 0) {
            model.jsonData = "empty"
        } else {
            model.setRecordList(resultSet.toList())
        }
        return model
    }

    fun filter(filter: FilterRecord, records: List<JsonObject> = getRecordList()): Model {
        return searchRecords(
            keySearch = filter.value,
            records = records,
            filter = filter
        )
    }


    fun hasField(fieldName: String): Boolean {
        getFieldList().forEach {
            if (it.fieldName == fieldName)
                return true
        }
        return false
    }

    fun hasIcon(): Boolean = icon != null

    fun clone(): Model {
        val model = Model()
        model.id = this.id
        model.name = this.name
        model.icon = this.icon
        model.jsonFields = this.jsonFields
        model.jsonData = this.jsonData
        model.sequence = this.sequence
        model.description = this.description
        return model
    }

    fun getDifferentFieldValue(field: Field, records: List<JsonObject> = getRecordList()): List<String> {
        val set = HashSet<String>()
        records.forEach {
            set.add(it.getValueOfField(field))
        }
        return set.toList()
    }

    fun getRecordsHasValueOfField(field: Field, value: String, records: List<JsonObject> = getRecordList()): List<JsonObject> {
        return searchRecords(
            keySearch = value,
            _withField = mutableListOf(field),
            searchMethod = "exact",
            records = records
        ).getRecordList()
    }

    fun getRecord(recordId: String, records: List<JsonObject> = getRecordList()): JsonObject? {
        records.forEach {
            if (it.getValueOfField("id") == recordId)
                return it
        }
        return null
    }


    private fun insertDefaultField(record: JsonObject) {
        val id = JsonObject()
        id.addProperty("fieldType", FieldType.TEXT.toString())
        id.addProperty("value", UUID.randomUUID().toString())

        val createTime = JsonObject()
        createTime.addProperty("fieldType", FieldType.DATETIME.toString())
        createTime.addProperty("value", System.currentTimeMillis().toString())

        record.add("id", id)
        record.add(getStr(R.string.default_field_create_time), createTime)
        record.add(getStr(R.string.default_field_last_update_time), createTime)
    }

    private fun insertUpdatetime(record: JsonObject) {
        val updateTime = JsonObject()
        updateTime.addProperty("fieldType", FieldType.DATETIME.toString())
        updateTime.addProperty("value", System.currentTimeMillis().toString())
        record.add(getStr(R.string.default_field_last_update_time), updateTime)
    }


    @JvmName("getFilePath1")
    fun getFilePath() = "${MyApplication.context.filesDir}/$name.json"

    fun writeJsonToFile() = writeFileText(getFilePath(), jsonData)
    fun deleteFileJson() = File(getFilePath()).delete()

    fun getJsonDataFromFile(): String = readFileAsTextUsingInputStream(getFilePath())
}

enum class FieldType {
    TEXT, NUMBER, DATETIME
}

class Field(var fieldName: String, var fieldType: FieldType) {
    var isChecked = false

    fun isValid(): Boolean = fieldName.trim() != ""

    fun isFieldCanSorted(): Boolean {
        return this.fieldType in arrayOf(
            FieldType.TEXT,
            FieldType.NUMBER,
            FieldType.DATETIME
        )
    }

    fun clone(): Field {
        return Field(fieldName, fieldType)
    }

    override fun toString(): String = "($fieldName: $fieldType)"

}

val defaultField = listOf("id", getStr(R.string.default_field_create_time), getStr(R.string.default_field_last_update_time))
fun String.isDefaultField() = this in defaultField

fun JsonObject.getValueOfField(fieldName: String): String {
    return this.get(fieldName).asJsonObject.get("value").asString
}

fun JsonObject.getValueOfField(field: Field): String {
    val value = getValueOfField(field.fieldName)
    return when (field.fieldType) {
        FieldType.NUMBER, FieldType.TEXT -> value
        FieldType.DATETIME -> DateTimeHelper.timestampToDatetimeString(value.toLong())
    }
}

fun JsonObject.getFieldType(fieldName: String): String {
    return this.get(fieldName).asJsonObject.get("fieldType").asString
}

fun JsonObject.updateFieldValue(fieldName: String, value: String): JsonObject {
    val jsonObj = this.deepCopy()
    jsonObj[fieldName].asJsonObject.addProperty("value", value)
    return jsonObj
}

fun Model.toHtmlTable(records: List<JsonObject> = getRecordList()): String {
    val html = """
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <style>
        table,
        td,
        th {
            border: 1px solid black;
            padding: 10px;
            border-collapse: collapse;
        }

        tr:nth-child(even) {
            background-color: #dddddd;
        }

        .record:active {
            background-color: rgb(81, 111, 246);
        }

        .field:active {
            background-color: #e58f8f;
        }

        .img-icon-sort {
            visibility: hidden;
            height: 15px;
        }

        img[active="true"] {
            visibility: visible;
        }
    </style>
</head>

<body>
    <table>


    </table>

    <script>
        function toStr(arr) {
            let s = '';
            arr.forEach(e => {
                s += e;
            });
            return s;
        }

        function sortByField(arr, fieldName, reverse) {
            let result = [...arr].sort((a, b) => (a[fieldName] > b[fieldName]) ? 1 : -1);
            if (reverse === 'true') {
                return result.reverse();
            }
            return result;
        }

        function loadDataToTable(thStr, trStr) {
            const table = document.getElementsByTagName('table')[0];
            tableValue = "<tr>{th_list}</tr>{td_list}"
                .replace('{th_list}', thStr)
                .replace('{td_list}', trStr)
            table.innerHTML = tableValue;
        }

        function toShow(fieldName) {
            return fieldName.replace('_', " ");
        }


        // mũi tên xuống là sắp xếp tăng dần, (mặc định)
       

        function getThStr(fieldList) {
            var thTemplate =
                "<th class=\"field\" field_name=\"{field_name_store}\">\n" +
                "      {field_name}\n" +
                "      <img class=\"img-icon-sort\" src=\"file:///android_res/drawable/icon_down.png\" temp=\"true\" >\n" +
                "</th>";

            var thStr = "<th></th>\n";

            for (fieldName of fieldList) {
                if (fieldName == 'id') {
                    continue;
                }
                if (fieldName == 'create_time') {
                    thStr += thTemplate.replace('{field_name}', toShow(fieldName))
                        .replace('{field_name_store}', fieldName)
                        .replace("temp=\"true\"", "active=\"true\"");
                } else {
                    thStr += thTemplate.replace('{field_name}', toShow(fieldName))
                        .replace('{field_name_store}', fieldName);
                }
            }
            return thStr;
        }



        function getTrStr(_data, fieldList) {
            let trStr = '';
            for (let i = 0; i < _data.length; i++) {
                let td = "<td>" + (i + 1) + ".</td>\n"

                const record = _data[i];
                const idRecord = record.id;

                for (const fieldName of fieldList) {
                    if (fieldName == 'id') {
                        continue;
                    }
                    const value = record[fieldName];
                    td += "<td>" + value + "</td>\n";
                }

                trStr += "<tr id=\"" + idRecord + "\"" + " class=\"record\">" + td + "</tr>\n";
            }
            return trStr;
        }

        function setClickRecord(onClick) {
            const trListHTML = Array.from(document.getElementsByClassName('record'));
            trListHTML.forEach(e => {
                e.addEventListener('click', () => {
                    const idRecord = e.getAttribute('id')
                    onClick(idRecord);
                });
            });
        }


        function hideAllImgSort() {
            const thListHTML = Array.from(document.getElementsByClassName('field'));
            thListHTML.forEach(th => {
                th.getElementsByTagName('img')[0].style.visibility = 'hidden';
            });
        }

        // loop set click for sort field.
        function loop() {
            const thListHTML = Array.from(document.getElementsByClassName('field'));
            thListHTML.forEach(th => {
                if (th.getAttribute('sort_up') == null) {
                    th.setAttribute('sort_up', 'false')
                }

                th.addEventListener('click', () => {
                    const fieldName = th.getAttribute('field_name');
                    console.log(fieldName);
                    const img = th.getElementsByTagName('img')[0]; // only one

                    hideAllImgSort()
                    img.style.visibility = 'visible';

                    let sortUp = th.getAttribute('sort_up');

                    if (sortUp === 'true') {
                        img.setAttribute('src', 'file:///android_res/drawable/icon_up.png');
                        th.setAttribute('sort_up', 'false')
                    } else if (sortUp === 'false') {
                        img.setAttribute('src', 'file:///android_res/drawable/icon_down.png');
                        th.setAttribute('sort_up', 'true')
                    }


                    console.log("Sort by: " + fieldName + " , revert: " + th.getAttribute('sort_up'))
                    const thListHTML = Array.from(document.getElementsByClassName('field'));
                    let thStr1 = "<th></th>" + toStr(thListHTML.map(e => e.outerHTML));

                    const dataSorted = sortByField(data, fieldName, th.getAttribute('sort_up'));

                    loadDataToTable(thStr1, getTrStr(dataSorted, fieldList));
                    loop();
                });
            });
            setClickRecord((idRecord) => {
                app.viewRecord(idRecord);
            })

        }

        // doesn't show id.
//        var fieldList = ["id", "name", "age", "create_time"];
//        var data = [
//            {
//                "id": "1",
//                "create_time": "2021-12-20",
//                "name": "Akame",
//                "age": 29,
//            },
//            {
//                "id": "2",
//                "create_time": "2021-12-23",
//                "name": "Bake",
//                "age": 12,
//            },
//            {
//                "id": "3",
//                "create_time": "2021-10-29",
//                "name": "Cukaz",
//                "age": 40,
//            },
//        ];
        {js-code: fieldList}
        {js-code: data}

        loadDataToTable(getThStr(fieldList), getTrStr(data, fieldList));
        loop();


    </script>
</body>

</html>
    """.trimIndent()

    val fieldList = getFieldList().toMutableList()
    fieldList.add(Field("id", FieldType.TEXT))
    fieldList.add(Field(getStr(R.string.default_field_create_time), FieldType.DATETIME))
    fieldList.add(Field(getStr(R.string.default_field_last_update_time), FieldType.DATETIME))

    var jsCodeFieldList = "var fieldList = [{fieldList}];"
    var temp = ""
    fieldList.forEach { temp += """ "${it.fieldName}", """ }
    jsCodeFieldList = jsCodeFieldList.replace("{fieldList}", temp)

    var jsCodeData = "var data = [{data}];"
    var temp1 = ""
    records.forEach { record ->
        var s = ""
        fieldList.forEach { field ->
            var value = ""
            try {
                value = record.getValueOfField(field)
                if (field.fieldType == FieldType.NUMBER) {
                }
            } catch (e: Exception) {}
            if (field.fieldType == FieldType.NUMBER) {
                s += """ "${field.fieldName}": ${value.toDouble()}, """
            } else {
                s += """ "${field.fieldName}": "$value", """
            }
        }
        s = "{$s},"
        temp1 += s
    }
    jsCodeData = jsCodeData.replace("{data}", temp1)

    return html.replace("{js-code: fieldList}",jsCodeFieldList)
               .replace(" {js-code: data}", jsCodeData)

}
