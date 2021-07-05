package dotd.hmp

import dotd.hmp.hepler.DateTimeHelper
import junit.framework.Assert.assertEquals
import org.junit.Test
import java.util.*

class TestDateTimeHelper {

    @Test
    fun testTimestampToDateString() {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, 2021)
        c.set(Calendar.MONTH, 11)
        c.set(Calendar.DAY_OF_MONTH, 20)
        c.set(Calendar.HOUR_OF_DAY, 8)
        c.set(Calendar.MINUTE, 10)
        c.set(Calendar.SECOND, 0)

        val datetimeString = DateTimeHelper.
            timestampToDatetimeString(c.timeInMillis, "yyyy-MM-dd hh:mm:ss")

        println(datetimeString)
        assertEquals(datetimeString, "2021-12-20 08:10:00")

        val current = DateTimeHelper.timestampToDatetimeString(System.currentTimeMillis())
        println(current)
    }

    @Test
    fun testMilisecondToDatetime() {
        val datetime = DateTimeHelper.milisecondToDatetime(120* 1000 + 1)
        println(datetime)

        assertEquals(datetime.minute, 2)
        assertEquals(datetime.seconds, 0)
        assertEquals(datetime.milisecond, 1)

        val datetime2 = DateTimeHelper.milisecondToDatetime(System.currentTimeMillis())
        println(datetime2)

    }

    @Test
    fun testTimeHasPassed() {
        val result = DateTimeHelper.getTimeHasPassed(1000 * 60 * 60, 1000*60*60*2 + 30*60*1000)
        println(result)
        assertEquals(result, "1 hours ago")

        val time1 = System.currentTimeMillis() - 30*60*1000
        val time2 = System.currentTimeMillis()
        val result2 = DateTimeHelper.getTimeHasPassed(time1, time2)
        println(result2)
        assertEquals(result2, "30 minutes ago")
    }

}