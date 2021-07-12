package dotd.hmp.data

import android.graphics.Color
import dotd.hmp.R
import dotd.hmp.hepler.getStr
import org.jetbrains.annotations.TestOnly
import java.util.*

object ModelDemoDatas {

    fun getModelStudentTest(): Model {
        val fieldList = mutableListOf<Field>()
        fieldList.add(Field(getStr(R.string.name), FieldType.TEXT))
        fieldList.add(Field(getStr(R.string.age), FieldType.NUMBER))

        val model = Model().apply {
            name = getStr(R.string.student)
            icon = R.drawable.ic_clear
            setFieldList(fieldList)
            jsonData = """
                [
                    {
                        "id": {
                            "fieldType": "${FieldType.TEXT}",
                            "value": "${UUID.randomUUID()}"
                        },
                        ${getStr(R.string.default_field_create_time)}: {
                            "fieldType": "${FieldType.DATETIME}",
                            "value": "${System.currentTimeMillis()}"
                        },
                        ${getStr(R.string.name)}: {
                            "fieldType": "${FieldType.TEXT}",
                            "value": "${getStr(R.string.name_demo1)}"
                        },
                        ${getStr(R.string.age)}: {
                            "fieldType": "${FieldType.NUMBER}", 
                            "value": "15"
                        }
                    },
                    {   
                        "id": {
                            "fieldType": "${FieldType.TEXT}",
                            "value": "${UUID.randomUUID()}"
                        },
                        ${getStr(R.string.default_field_create_time)}: {
                            "fieldType": "${FieldType.DATETIME}",
                            "value": "${System.currentTimeMillis()}"
                        },
                        ${getStr(R.string.name)}: {
                            "fieldType": "${FieldType.TEXT}",
                            "value":  "${getStr(R.string.name_demo2)}"
                        },
                        ${getStr(R.string.age)}: {
                            "fieldType": "${FieldType.NUMBER}", 
                            "value": "16"
                        }
                    },
                    {
                        "id": {
                            "fieldType": "${FieldType.TEXT}",
                            "value": "${UUID.randomUUID()}"
                        },
                        ${getStr(R.string.default_field_create_time)}: {
                            "fieldType": "${FieldType.DATETIME}",
                            "value": "${System.currentTimeMillis()}"
                        },
                        ${getStr(R.string.name)}: {
                            "fieldType": "${FieldType.TEXT}",
                            "value": "${getStr(R.string.name_demo3)}"
                        },
                        ${getStr(R.string.age)}: {
                            "fieldType": "${FieldType.NUMBER}",  
                            "value": "12"
                        }
                    }
                    
                ]
            """
        }
        return model
    }

    @TestOnly
    fun getModelStudentTest(recordCount: Int): Model {
        val fieldList = mutableListOf<Field>()
        fieldList.add(Field("name", FieldType.TEXT))
        fieldList.add(Field("age", FieldType.NUMBER))
        fieldList.add(Field("birth_day", FieldType.DATETIME))

        val model = Model().apply {
            name = "Student Test"
            icon = R.drawable.ic_clear
            setFieldList(fieldList)
        }
        var jsonData = ""
        for (i in 1..recordCount) {
            jsonData += """
                {
                        "id": {
                            "fieldType": "${FieldType.TEXT}",
                            "value": "${UUID.randomUUID()}"
                        },
                        "name": {
                            "fieldType": "${FieldType.TEXT}",
                            "value": "Peter $i"
                        },
                        "age": {
                            "fieldType": "${FieldType.NUMBER}", 
                            "value": "${Random().nextInt(100000)}"
                        },
                        "birth_day": {
                            "fieldType": "${FieldType.DATETIME}",
                            "value": "${System.currentTimeMillis()}"
                        },
                        "${getStr(R.string.default_field_create_time)}": {
                            "fieldType": "${FieldType.DATETIME}",
                            "value": "${System.currentTimeMillis()}"
                        }
                }
            """.trim().replace("\n", "").replace(" ", "")
            if (i != recordCount)
                jsonData += ","
        }
        jsonData = "[$jsonData]"
        model.jsonData = jsonData
        return model
    }
}
