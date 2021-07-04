package dotd.hmp.hepler

import java.text.SimpleDateFormat
import java.util.*

class DateTime {
    var year = 0
    var month = 0
    var day = 0
    var hour = 0
    var minute = 0
    var second = 0
    var milisecond = 0

    override fun toString(): String =
        "Datetime(year=$year, month=$month, day=$day, hour=$hour, minute=$minute, second=$second, milisecond=$milisecond)"

}

object DateTimeHelper {
    fun timestampToDateString(timestamp: Long, format: String = "yyyy-MM-dd hh:mm:ss [E]"): String {
        val sdf = SimpleDateFormat(format)
        val date = Date(timestamp)
        return sdf.format(date)
    }

    fun milisecondToDatetime(milisecond: Long): DateTime {
        var s = milisecond

        val year = s / (1000*60*60*24*30*12)
        s %= (1000*60*60*24*30*12)
        val month = s / (1000*60*60*24*30)
        s %= (1000*60*60*24*30)
        val day = s / (1000*60*60*24)
        s %= (1000*60*60*24)
        val hour = s / (1000*60*60)
        s %= (1000*60*60)
        val minute = s / (1000*60)
        s %= (1000*60)
        val second = s / 1000
        s %= 1000
        val milisecond1  = s

        return DateTime().apply {
            this.year = year.toInt()
            this.month = month.toInt()
            this.day = day.toInt()
            this.hour = hour.toInt()
            this.minute = minute.toInt()
            this.second = second.toInt()
            this.milisecond = milisecond1.toInt()
        }
    }

}