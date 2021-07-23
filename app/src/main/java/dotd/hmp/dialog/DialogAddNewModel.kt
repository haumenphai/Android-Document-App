package dotd.hmp.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import dotd.hmp.R
import dotd.hmp.data.Model
import dotd.hmp.databinding.DialogAddNewModelBinding
import dotd.hmp.hepler.UIHelper
import dotd.hmp.hepler.setImageAssets

class DialogAddNewModel(val context: Context) {
    private val view by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_add_new_model, null) }
    val b by lazy { DialogAddNewModelBinding.bind(view) }
    val dialog by lazy { Dialog(context) }

    private var pathIcon = Model.defaultIcon

    init {
        dialog.setContentView(b.root)
        dialog.setCanceledOnTouchOutside(false)
        b.imgIcon.setImageAssets(pathIcon)
    }

    fun setBtnOkClick(callBack: (modelName: String, pathIcon: String) -> Unit) {

        b.btnOk.setOnClickListener {
            val text = b.edittext.text.toString()
            if (text.trim().isEmpty()) {
                AlertDialog.Builder(context).setMessage(context.getString(R.string.model_name_must_not_be_empty))
                return@setOnClickListener
            }
            callBack(text, pathIcon)
            cancel()
        }

        // call other dialog
        b.imgIcon.setOnClickListener {
            UIHelper.hideKeyboardFrom(context, b.edittext)
            DialogPickIcon(context).apply {
                setItemIconClick { itemIcon ->
                    pathIcon = itemIcon.pathIcon
                    cancel()
                    this@DialogAddNewModel.b.imgIcon.setImageAssets(pathIcon)
                }
                show()
            }
        }

    }

    fun setBtnCancelClick(callBack: () -> Unit) {
        b.btnCancel.setOnClickListener {
            callBack()
            cancel()
        }
    }

    fun cancel() = dialog.cancel()
    fun show() = dialog.show()

}
