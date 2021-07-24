package dotd.hmp.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import dotd.hmp.R
import dotd.hmp.databinding.DialogShowTextLargeBinding
import dotd.hmp.hepler.setTextHTML

class DialogShowTextLarge(private val context: Context) {
    private val view by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_show_text_large, null) }
    private val b by lazy { DialogShowTextLargeBinding.bind(view) }
    private val dialog by lazy {
        Dialog(context).apply {
            setContentView(view)
        }
    }

    fun show(): DialogShowTextLarge {
        dialog.show()
        return this
    }

    fun cancel(): DialogShowTextLarge {
        dialog.cancel()
        return this
    }

    fun setTitle(title: String): DialogShowTextLarge {
        b.tvTitle.text = title
        return this
    }

    fun setContent(content: String): DialogShowTextLarge {
        b.tvContent.text = content
        return this
    }

    fun setContentHTML(html: String): DialogShowTextLarge {
        b.tvContent.setTextHTML(html)
        return this
    }


}
