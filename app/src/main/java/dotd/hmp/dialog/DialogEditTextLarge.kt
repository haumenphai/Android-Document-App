package dotd.hmp.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import dotd.hmp.R
import dotd.hmp.databinding.DialogShowEdittextLargeBinding

class DialogEditTextLarge(val context: Context, val text: String) {

    private val view by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_show_edittext_large, null) }
    val b by lazy { DialogShowEdittextLargeBinding.bind(view) }
    val dialog by lazy { Dialog(context) }

    init {
        dialog.setContentView(b.root)
        b.edittext.setText(text)
    }

    fun setOnCancelListener(callBack: (text: String) -> Unit) {
        dialog.setOnCancelListener {
            callBack.invoke(b.edittext.text.toString())
        }
    }

    fun show() = dialog.show()
    fun cancel() = dialog.cancel()
}