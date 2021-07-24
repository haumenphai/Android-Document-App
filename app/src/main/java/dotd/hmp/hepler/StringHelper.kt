package dotd.hmp.hepler

import dotd.hmp.MyApplication
import java.text.Normalizer
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * "hello world xx".title() -> "Hello world xx"
 * "Hello World".title() -> "Hello World"
 */
fun String.title(): String {
    if (this.isEmpty()) return this
    var result = this.substring(0, 1).toUpperCase(Locale.ROOT)
    result += this.substring(1).toLowerCase(Locale.ROOT)
    return result
}

fun String.toFieldNameShow(): String = this.replace("_", " ")
fun String.toFieldNameStore(): String = this.replace(" ", "_")

/**
 * "xin chào thế 123 an".removeVietnameseAccents() -> "xin chao the 123 an"
 */
fun String.removeVietnameseAccents(): String {
    var s = this
    s = Normalizer.normalize(s, Normalizer.Form.NFD)
    s = s.replace("[\\p{InCombiningDiacriticalMarks}]".toRegex(), "")
    s = s.replace("Đ", "D")
    s = s.replace("đ", "d")
    return s
}

/**
 * Get string resource of android
 */
fun getStr(res: Int): String = MyApplication.context.getString(res)

/**
 * "do do123,do xx do".count("do") -> 4
 * "11111".count("1") -> 5
  */
fun String.count(str: String): Int {
    val pattern = Pattern.compile(str)
    val matcher = pattern.matcher(this)
    var result = 0
    while (matcher.find()) {
        result += 1
    }
    return result
}


/**
 *  "sx 123.01xx 9123 123.2".getListNumber() -> listOf("123.01", "9123", "123.2")
 */
fun String.getListNumber(): List<String> {
    val pattern = Pattern.compile("(\\d)+(\\.)?(\\d)+")
    val matcher = pattern.matcher(this)
    val list = mutableListOf<String>()
    while (matcher.find()) {
        list.add(this.substring(matcher.start(), matcher.end()))
    }
    return list
}

/**
 * "sx 123.01xx 9123 123.2".getStartFirstNumber() -> null
 * "123.01xx 9123 123.2".getStartFirstNumber() -> "123.01"
 * "123xx 9123 123.2".getStartFirstNumber() -> "123"
 */
fun String.getStartFirstNumber(): String? {
    if (this[0] !in '0'..'9')
        return null
    val list = this.getListNumber()
    if (list.isNotEmpty()) {
        return list[0]
    }
    return null
}

/*
    "x 123.01"      .isNumber() -> false
    "x 123"         .isNumber() -> false
    "123.02 xx"     .isNumber() -> false
    "123"           .isNumber() -> true
    "123.02"        .isNumber() -> true
 */
fun String.isNumber(): Boolean {
    val regex = "^(\\d)+(\\.)?(\\d)+"
    return Pattern.matches(regex, this)
}
