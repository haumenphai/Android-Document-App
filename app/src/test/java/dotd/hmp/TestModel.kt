package dotd.hmp

import dotd.hmp.data.*
import org.junit.Assert
import org.junit.Test
import java.util.*

class TestModel {

    // data for test, must not be changed
    private fun getDataTest(): Model {
        val fieldList = mutableListOf<Field>()
        fieldList.add(Field("name", FieldType.TEXT))
        fieldList.add(Field("age", FieldType.NUMBER))

        val model = Model().apply {
            name = "Student"
            icon = R.drawable.ic_clear
            setFieldList(fieldList)
            jsonData = """
                [
                    {
                        "id": {
                            "fieldType": "${FieldType.TEXT}",
                            "value": "${UUID.randomUUID()}"
                        },
                        "create_time": {
                            "fieldType": "${FieldType.DATETIME}",
                            "value": "${System.currentTimeMillis()}"
                        },
                        "name": {
                            "fieldType": "${FieldType.TEXT}",
                            "value": "Aeter"
                        },
                        "age": {
                            "fieldType": "${FieldType.NUMBER}", 
                            "value": "20"
                        }
                    },
                    {   
                        "id": {
                            "fieldType": "${FieldType.TEXT}",
                            "value": "${UUID.randomUUID()}"
                        },
                        "create_time": {
                            "fieldType": "${FieldType.DATETIME}",
                            "value": "${System.currentTimeMillis()}"
                        },
                        "name": {
                            "fieldType": "${FieldType.TEXT}",
                            "value": "Ceter"
                        },
                        "age": {
                            "fieldType": "${FieldType.NUMBER}", 
                            "value": "30"
                        }
                    },
                    {
                        "id": {
                            "fieldType": "${FieldType.TEXT}",
                            "value": "${UUID.randomUUID()}"
                        },
                        "create_time": {
                            "fieldType": "${FieldType.DATETIME}",
                            "value": "${System.currentTimeMillis()}"
                        },
                        "name": {
                            "fieldType": "${FieldType.TEXT}",
                            "value": "Bocke"
                        },
                        "age": {
                            "fieldType": "${FieldType.NUMBER}",  
                            "value": "19"
                        }
                    }
                    
                ]
            """.replace(" ", "").replace("\n", "")
        }
        return model
    }

    @Test
    fun testDeleteRecord() {
        val model = getDataTest()
        val recordList = model.getRecordList()

        model.deleteRecord(recordList[0])
        model.deleteRecord( recordList[2])

        Assert.assertEquals(model.getRecordList().size, 1)
        Assert.assertEquals(model.getRecordList()[0].getValueOfField("name"), "Ceter")
    }

    @Test
    fun testUpdateRecord() {
        val model = getDataTest()
        val record1 = model.getRecordList()[0]

        model.updateRecord(record1.updateFieldValue("age", "88"))
        Assert.assertEquals(model.getRecordList()[0].getValueOfField("age"), "88")
    }

    @Test
    fun testAddRecord() {
        val model = getDataTest()
        model.addRecord(model.getRecordList()[0])

        Assert.assertEquals(model.getRecordList()[0].getValueOfField("name"),
            model.getRecordList()[3].getValueOfField("name"))
    }

    @Test
    fun testSortRecord() {
        var model = getDataTest()
        model = model.sortByField("age")
        Assert.assertEquals(model.getRecordList()[0].getValueOfField("age"), "19")
        Assert.assertEquals(model.getRecordList()[1].getValueOfField("age"), "20")
        Assert.assertEquals(model.getRecordList()[2].getValueOfField("age"), "30")

        model = model.sortByField("name")
        Assert.assertEquals(model.getRecordList()[0].getValueOfField("name"), "Aeter")
        Assert.assertEquals(model.getRecordList()[1].getValueOfField("name"), "Bocke")
        Assert.assertEquals(model.getRecordList()[2].getValueOfField("name"), "Ceter")
    }

    @Test
    fun testGetListRecord() {
        val model = getDataTest()
        Assert.assertEquals(model.getRecordList().toString().replace(" ", ""), model.jsonData)
    }
}