package dotd.hmp.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import dotd.hmp.R
import dotd.hmp.databinding.DialogConfirmBinding

class DialogConfirm(private val context: Context) {
    private val view by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null) }
    private val b by lazy { DialogConfirmBinding.bind(view) }
    private val dialog by lazy { Dialog(context) }

    init {
        dialog.setContentView(view)
    }

    fun show(): DialogConfirm {
        dialog.show()
        return this
    }

    fun cancel(): DialogConfirm {
        dialog.cancel()
        return this
    }

    fun setCancelOnTouchOutSide(cancel: Boolean): DialogConfirm {
        dialog.setCanceledOnTouchOutside(cancel)
        return this
    }

    fun setCancelable(cancel: Boolean): DialogConfirm {
        dialog.setCancelable(cancel)
        return this
    }

    fun setBtnOkClick(onClick: (d: DialogConfirm) -> Unit): DialogConfirm {
        b.btnOk.setOnClickListener {
            onClick(this)
        }
        return this
    }

    fun setBtnCancelClick(onClick: (d: DialogConfirm) -> Unit): DialogConfirm {
        b.btnCancel.setOnClickListener {
            onClick(this)
        }
        return this
    }

    fun setTitle(title: String): DialogConfirm {
        b.tvTitle.text = title
        return this
    }

    fun setMess(mess: String): DialogConfirm {
        b.tvMess.text = mess
        return this
    }

    fun setTextBtnOk(text: String): DialogConfirm {
        b.btnOk.text = text
        return this
    }

    fun setTextBtnCancel(text: String): DialogConfirm {
        b.btnCancel.text = text
        return this
    }

}