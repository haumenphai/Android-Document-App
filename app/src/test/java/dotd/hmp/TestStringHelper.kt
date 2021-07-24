package dotd.hmp

import dotd.hmp.hepler.count
import dotd.hmp.hepler.getStartFirstNumber
import dotd.hmp.hepler.getListNumber
import dotd.hmp.hepler.isNumber
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test

class TestStringHelper {

    @Test
    fun testCount() {
        val s = "do 123 dodo 123, do"
        assertEquals(s.count("do"), 4)
        val s1  = "1111"
        println(s1.count("1"))
    }

    @Test
    fun tesGetListNumber() {
        val s = "sx 123.01xx 9123 123.2"
        val result = s.getListNumber()
        assertEquals(result, listOf("123.01", "9123", "123.2"))
    }

    @Test
    fun testGetFirstNumber() {
        val s1 = "sx 123.01xx 9123 123.2"
        val s2 = "123.01xx 9123 123.2"
        val s3 = "123xx 9123 123.2"

        val result1 = s1.getStartFirstNumber()
        val result2 = s2.getStartFirstNumber()
        val result3 = s3.getStartFirstNumber()
        assertEquals(result1, null)
        assertEquals(result2, "123.01")
        assertEquals(result3, "123")
    }

    @Test
    fun testIsNumber() {
        val s1 = "x 123.01"
        val s2 = "x 123"
        val s3 = "123"
        val s4 = "123.02"
        val s5 = "123.02 xx"

        assertEquals(s1.isNumber(), false)
        assertEquals(s2.isNumber(), false)
        assertEquals(s3.isNumber(), true)
        assertEquals(s4.isNumber(), true)
        assertEquals(s5.isNumber(), false)
    }
}
