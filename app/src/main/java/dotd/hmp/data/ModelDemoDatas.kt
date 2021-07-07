package dotd.hmp.data

import android.graphics.Color
import dotd.hmp.R
import java.util.*

object ModelDemoDatas {

    fun getModelStudent(): Model {
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
                            "value": "40"
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
}
