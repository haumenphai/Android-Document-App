package dotd.hmp.hepler

import dotd.hmp.MyApplication
import java.util.*

fun String.title(): String {
    if (this.isEmpty()) return this
    var result = this.substring(0,1).toUpperCase(Locale.ROOT)
    result += this.substring(1).toLowerCase(Locale.ROOT)
    return result
}

fun String.toFieldNameShow(): String = this.replace("_", " ")
fun String.toFieldNameStore(): String = this.replace(" ", "_")

fun getStr(res: Int): String = MyApplication.context.getString(res)