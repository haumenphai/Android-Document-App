package dotd.hmp.data

import android.graphics.Color
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dotd.hmp.R
import org.json.JSONObject
import java.io.Serializable
import java.lang.Exception
import java.util.*


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
            "value": 12031200222
       },
       "name": {
            "fieldType": "TEXT",
            "value": "Peter"
        },
        "age": {
            "fieldType": "NUMBER",
            "value": 20
        }
    },
]

 */

@Entity
class Model: Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var name: String = ""
    var icon: Int? = null
    var jsonFields: String = ""
    var jsonData: String = "[]"
    var sequence: Int = 0
    var description = ""

    constructor()

    @Ignore
    constructor(name: String, icon: Int) {
        this.name = name
        this.icon = icon
    }

    companion object {
        val itemAddNewModel by lazy {
                Model("Add", Color.RED).apply {
                    description = "add new model"
                    icon = R.drawable.ic_baseline_add_24
            }
        }
    }

    fun setFieldList(list: MutableList<Field>) {
        this.jsonFields = Gson().toJson(list)
    }

    fun getFieldList(): List<Field> = Gson().fromJson(this.jsonFields, Array<Field>::class.java).asList()

    fun addNewRecord(jsonObj: org.json.JSONObject) {
        if (!jsonObj.isJsonObjRecordValidate()) {
            throw Exception("Exception when add new record, jsonOject not enough field when compared to fieldList of Model")
        }
        insertDefaultField(jsonObj)

        val jsonArray = getJsonArray()
        jsonArray.add(Gson().fromJson(jsonObj.toString(), JsonObject::class.java))
        jsonData = jsonArray.toString()
    }

    fun getJsonArray(): com.google.gson.JsonArray = Gson().fromJson(jsonData, JsonArray::class.java)

    private fun org.json.JSONObject.isJsonObjRecordValidate(): Boolean {
        getFieldList().forEach {
            if (!this.has(it.fieldName)) {
                return false
            }
        }
        return true
    }

    private fun insertDefaultField(jsonObj: JSONObject) {
        val id = JSONObject()
        id.put("fieldType", FieldType.TEXT)
        id.put("value", UUID.randomUUID().toString())

        val createTime = JSONObject()
        createTime.put("fieldType", FieldType.DATETIME)
        createTime.put("value", System.currentTimeMillis())

        jsonObj.put("id", id)
        jsonObj.put("createTime", createTime)
    }

    fun sortByField(fieldName: String): Model {
        val jsonArr = getJsonArray()
        if (!hasField(fieldName) || jsonArr.size() == 0) {
            return this
        }

        val model = this.clone()
        val jsonData = getJsonArray().sortedWith(compareBy(
            {it.asJsonObject.get(fieldName).asJsonObject.get("value").asString},
            {it.asJsonObject.get(fieldName).asJsonObject.get("value").asString}
        )).toString()
        model.jsonData = jsonData

        return model
    }

    fun hasField(fieldName: String): Boolean {
        getFieldList().forEach {
            if (it.fieldName == fieldName)
                return true
        }
        return false
    }

    fun isItemAddNewModel(): Boolean = itemAddNewModel == this

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

}

enum class FieldType {
    TEXT, NUMBER, DATETIME
}

class Field(var fieldName: String, var fieldType: FieldType) {
    fun isValid(): Boolean = fieldName.trim() != ""
}

val defaultField = arrayOf("id", "createTime")
fun String.isDefaultField() = this in defaultField

