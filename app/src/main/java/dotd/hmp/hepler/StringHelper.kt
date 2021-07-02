package dotd.hmp.hepler

import java.util.*

fun String.title(): String {
    if (this.isEmpty()) return this
    var result = this.substring(0,1).toUpperCase(Locale.ROOT)
    result += this.substring(1).toLowerCase(Locale.ROOT)
    return result
}