package dotd.hmp.data

import android.graphics.Color
import dotd.hmp.R

object ModelDemoDatas {
    fun getDemoDatas(): List<Model> {
        val list = mutableListOf<Model>()
        list.add(Model("Student", R.drawable.ic_clear))
        return list
    }

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
                    {"name": "Peter", age: 12},
                    {"name": "Beem", "age": 18},
                    {"name": "Muckey", "age": 17}
                ]
            """.trimIndent()
        }
        return model
    }
}
