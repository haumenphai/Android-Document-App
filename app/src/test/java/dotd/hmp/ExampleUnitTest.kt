package dotd.hmp

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dotd.hmp.data.Field
import dotd.hmp.data.FieldType
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun myTest() {
        val list = mutableListOf<Field>()
        list.add(Field("name", FieldType.TEXT))
        list.add(Field("age", FieldType.NUMBER))

        val json = Gson().toJson(list)
        println(json)

        val fieldList = Gson().fromJson(json, Array<Field>::class.java).asList()
        println(fieldList[0].fieldName)
        println(fieldList[0].fieldType)
        println(fieldList.size)

    }

    @Test
    fun test2() {
        val list = listOf(Field("name", FieldType.TEXT))
        println(list.toString())
    }


    fun androidJson() {
        val json = """
                [
                    {"name": "do", "age": 20},
                    {"name": "Mickye", "age": 10}
                ]
            """
        val gson = Gson()
        val s =  gson.fromJson(json, JsonArray::class.java)
//
//            val jsonObject = org.json.JSONObject()
//
//            val linkedTreeMap = LinkedTreeMap<String, String>()
//            linkedTreeMap.put("name", )
//
        Log.d("CCC", s::class.simpleName.toString()) // Arraylist


        val j1 = JSONObject()
        j1.put("name", "Peter")
        j1.put("age", 20)
        Log.d("CCC", j1.toString())


        s.add(gson.fromJson(j1.toString(), JsonObject::class.java))

        Log.d("CCC", s.toString())
    }

    @Test
    fun tedt() {
        val list = mutableListOf(1, 2, 3, 4, 5, 6)
        val l1 = list.subList(0, 2)
        val l2 = list.subList(2, 4)
        val l3 = list.subList(4, 6)

        println(l1)
        println(l2)
        println(l3)


        println(list.chunked(list.size))
    }

    @Test
    fun test22() {
        println("13" >= "14")
    }


}
