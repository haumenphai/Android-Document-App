package dotd.hmp.hepler

import java.text.SimpleDateFormat
import java.util.*

// todo: improve DateTimeHelper.kt for exact calculation
class DateTime {
    var year = 0
    var month = 0
    var day = 0
    var hour = 0
    var minute = 0
    var seconds = 0
    var milisecond = 0

    override fun toString(): String =
        "Datetime(year=$year, month=$month, day=$day, hour=$hour, minute=$minute, second=$seconds, milisecond=$milisecond)"

    fun format(format: String = "yyyy-MM-dd hh:mm:ss [E]"): String {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, day)
        c.set(Calendar.HOUR_OF_DAY, hour)
        c.set(Calendar.MINUTE, minute)
        c.set(Calendar.SECOND, seconds)

        val simpleDateFormat = SimpleDateFormat(format)
        return simpleDateFormat.format(c.time)
    }

    fun toMiliseconds(): Long {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, day)
        c.set(Calendar.HOUR_OF_DAY, hour)
        c.set(Calendar.MINUTE, minute)
        c.set(Calendar.SECOND, seconds)

        return c.timeInMillis
    }
}

object DateTimeHelper {
    fun timestampToDatetimeString(timestamp: Long, format: String = "yyyy-MM-dd hh:mm:ss [E]"): String {
        val sdf = SimpleDateFormat(format)
        val date = Date(timestamp)
        return sdf.format(date)
    }

    /**
     * The results are relative, ignoring the month with 31 days, leap years, ...
     */
    fun milisecondToDatetime(milliseconds: Long): DateTime {
        var s = milliseconds

        // kotlin incorrect calculation: 12*30*24*60*60*1000
        val year = s / (31104000000)
        s %= (31104000000)
        val month = s / (2592000000)
        s %= (2592000000)
        val day = s / (1000*60*60*24)
        s %= (1000*60*60*24)
        val hour = s / (1000*60*60)
        s %= (1000*60*60)
        val minute = s / (1000*60)
        s %= (1000*60)
        val second = s / 1000
        s %= 1000
        val miliseconds1  = s

        return DateTime().apply {
            this.year = year.toInt()
            this.month = month.toInt()
            this.day = day.toInt()
            this.hour = hour.toInt()
            this.minute = minute.toInt()
            this.seconds = second.toInt()
            this.milisecond = miliseconds1.toInt()
        }
    }

    /**
     * get relative time has passed
     */
    fun getTimeHasPassed(timestamp: Long, current: Long = System.currentTimeMillis()): String {
        // todo: replace mess = getString for translate
        val now = "now"
        val yearsAgo = "years ago"
        val monthsAgo = "months ago"
        val daysAgo = "days ago"
        val hoursAgo = "hours ago"
        val minutesAgo = "minutes ago"

        val dateTime = milisecondToDatetime(current - timestamp)

        return when {
            dateTime.year > 0 -> {
                "${dateTime.year} $yearsAgo"
            }
            dateTime.month > 0 -> {
                "${dateTime.month} $monthsAgo"
            }
            dateTime.day > 0 -> {
                "${dateTime.day} $daysAgo"
            }
            dateTime.hour > 0 -> {
                "${dateTime.hour} $hoursAgo"
            }
            dateTime.minute > 0 -> {
                "${dateTime.minute} $minutesAgo"
            }
            else -> now
        }
    }

}