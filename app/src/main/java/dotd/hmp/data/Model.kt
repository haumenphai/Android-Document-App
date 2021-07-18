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
        <html>
        <head>
            <style>
                table {
                    font-family: arial, sans-serif;
                    border-collapse: collapse;
                }

                td, th {
                    border: 1px solid black;
                    text-align: left;
                    padding: 8px;
                }

                tr:nth-child(even) {
                    background-color: #dddddd;
                }
            </style>
        </head>
        <body>
            <table>
                <tr>%s</tr>
                %s
            </table>
        </body>

        </html>
    """.trimIndent()

    val fieldList = getFieldList().toMutableList()
    fieldList.add(Field(getStr(R.string.default_field_create_time), FieldType.DATETIME))
    fieldList.add(Field(getStr(R.string.default_field_last_update_time), FieldType.DATETIME))

    var tr1 = ""
    tr1 += "<th></th>"
    fieldList.forEach {
        tr1 += "<th>${it.fieldName.toFieldNameShow()}</th>"
    }

    var trList = ""

    for ((i,v) in records.withIndex()) {
        var td = "<td>${i+1}.</td>"

        fieldList.forEach { field ->
            var value = ""
            try {
                value = v.getValueOfField(field)
            } catch (e: Exception) {}
            td += "<td>$value</td>"
        }
        trList += "<tr>$td</tr>"
    }



   return html.format(tr1, trList)
}
