package dotd.hmp.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import dotd.hmp.R
import dotd.hmp.databinding.DialogConfirmCreateModelBinding

class DialogConfirmCreateModel(val context: Context) {
    private val view by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_confirm_create_model, null) }
    val b by lazy { DialogConfirmCreateModelBinding.bind(view) }
    val dialog by lazy { Dialog(context) }

    init {
        dialog.setContentView(view)
    }

}