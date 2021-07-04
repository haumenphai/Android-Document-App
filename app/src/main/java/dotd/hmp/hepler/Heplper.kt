package dotd.hmp.hepler

import android.os.Build
import android.text.Html
import android.widget.TextView

fun TextView.setTextHTML(html: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE)
    } else {
        this.setText(Html.fromHtml(html), TextView.BufferType.SPANNABLE)
    }
}