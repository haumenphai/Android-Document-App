package dotd.hmp

import com.google.gson.Gson
import dotd.hmp.data.Field
import dotd.hmp.data.FieldType
import org.junit.Test

import org.junit.Assert.*

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
}