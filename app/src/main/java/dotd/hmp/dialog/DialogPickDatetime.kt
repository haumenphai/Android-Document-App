package dotd.hmp.dialog

import android.app.DatePickerDialog
import android.content.Context
import com.ikovac.timepickerwithseconds.MyTimePickerDialog
import dotd.hmp.hepler.DateTime
import java.util.*

class DialogPickDatetime {
    companion object {
        @JvmStatic
        fun show(context: Context, callBack: (dateTime: DateTime) -> Unit) {
            val c = Calendar.getInstance()
            val dateTime = DateTime()
            val year1 = c.get(Calendar.YEAR)
            val month1 = c.get(Calendar.MONTH)
            val day1 = c.get(Calendar.DAY_OF_MONTH)
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val second = c.get(Calendar.SECOND)

            DatePickerDialog(context, { view, year, monthOfYear, dayOfMonth ->
                dateTime.year = year
                dateTime.month = monthOfYear
                dateTime.day = dayOfMonth

                MyTimePickerDialog(context, { v, hourOfDay, minute, seconds ->
                    dateTime.hour = hourOfDay
                    dateTime.minute = minute
                    dateTime.seconds = seconds
                    callBack(dateTime)
                }, hour, minute, second, true).show()
            }, year1, month1, day1).show()
        }
    }
}