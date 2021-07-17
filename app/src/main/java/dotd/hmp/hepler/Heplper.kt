package dotd.hmp.hepler

import android.os.Build
import android.text.Html
import android.widget.TextView
import java.util.*
import kotlin.collections.HashMap

fun TextView.setTextHTML(html: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE)
    } else {
        this.setText(Html.fromHtml(html), TextView.BufferType.SPANNABLE)
    }
}

fun <T> List<T>.toListCopy(): List<T> =
    mutableListOf<T>().apply { addAll(this) }

fun <T> List<T>.toMutableListCopy(): MutableList<T> =
    mutableListOf<T>().apply { addAll(this) }

fun <K, V> Map<K, V>.getKey(value: V): K? {
    for ((key, value1) in this) {
        if (Objects.equals(value, value1)) {
            return key
        }
    }
    return null
}