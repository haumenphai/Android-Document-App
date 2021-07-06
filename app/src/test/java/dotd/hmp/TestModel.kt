package dotd.hmp

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dotd.hmp.data.*
import junit.framework.Assert.assertEquals
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test

class TestModel {
    @Test
    fun testDeleteRecord() {
        val model = ModelDemoDatas.getModelStudent()
        println(model.jsonData)
        println(model.getRecordArray().size())

        val record1 = model.getRecordArray().get(0).asJsonObject
        val record2 = model.getRecordArray().get(1).asJsonObject
        val record3 = model.getRecordArray().get(2).asJsonObject

        model.deleteRecord(record1)
        model.deleteRecord(record2)
        model.deleteRecord(record3)

        println(model.jsonData)
        println(model.getRecordArray().size())
        Assert.assertEquals(model.getRecordArray().size(), 0)
    }

    @Test
    fun testUpdateRecord() {
        val model = ModelDemoDatas.getModelStudent()
        val record1 = model.getRecordArray().get(0).asJsonObject
        println(model.jsonData)

        model.updateRecord(record1.updateFieldValue("age", "88"))
        println(model.jsonData)

        Assert.assertEquals(model.getRecordArray()[0].asJsonObject.get("age").asJsonObject.get("value").asString, "88")

    }

    @Test
    fun testAddRecord() {
        val model = Model()
        model.setFieldList(mutableListOf(Field("name", FieldType.TEXT)))
        val record = JsonObject()

        record.addProperty("name", "Do")

        model.addRecord(record)
        println(model.jsonData)
    }

}