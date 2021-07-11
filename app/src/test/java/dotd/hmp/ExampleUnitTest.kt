package dotd.hmp

import android.icu.util.Calendar
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dotd.hmp.data.Field
import dotd.hmp.data.FieldType
import dotd.hmp.hepler.DateTimeHelper
import org.json.JSONObject
import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Calendar.YEAR
import kotlin.time.Duration

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
//        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss [E]")
//        val date = Date(System.currentTimeMillis())
//        println(sdf.format(date))

        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss [E]")
        val differenceTime = System.currentTimeMillis() - 1000233467
        val date = Date(differenceTime)
        val calendar = java.util.Calendar.getInstance()
        calendar.time = date

        println(date)
        println(sdf.format(date))
        println(calendar.get(Calendar.DAY_OF_MONTH))
        println(LocalDateTime.MIN.plusSeconds(121).second)

        println("2021-12-30 20:00:01 TH" > "2021-12-30 20:00:00 TH2")
    }
}
