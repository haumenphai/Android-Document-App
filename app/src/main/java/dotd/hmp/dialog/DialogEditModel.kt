package dotd.hmp.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import dotd.hmp.R
import dotd.hmp.data.Model
import dotd.hmp.databinding.DialogEditModelBinding

class DialogEditModel(private val context: Context, private val model: Model) {
    private val view by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_edit_model, null) }
    private val b by lazy { DialogEditModelBinding.bind(view) }
    private val dialog by lazy { Dialog(context) }
    private val modelCopy = model.clone()

    init {
        dialog.setContentView(view)
        dialog.setCanceledOnTouchOutside(false)
        b.editModelName.setText(model.name)
        b.editModelName.setSelection(model.name.length)

        if (model.icon != null)
            b.imgIcon.setImageResource(model.icon!!)

        b.imgIcon.setOnClickListener {
            DialogPickIcon(context).setItemIconClick {
                modelCopy.icon = it.drawableResource
                b.imgIcon.setImageResource(it.drawableResource)
            }.show()
        }
    }

    fun setBtnSaveClick(onClick: (modelEdited: Model) -> Unit): DialogEditModel {
        b.btnSave.setOnClickListener {
            val name = b.editModelName.text.toString()
            if (name.trim().isEmpty()) {
                AlertDialog.Builder(context).setMessage(context.getString(R.string.name_must_not_be_empty)).show()
                return@setOnClickListener
            }

            modelCopy.name = name
            onClick(modelCopy)
            cancel()
        }
        return this
    }

    fun setBtnCancelClick(onClick: () -> Unit): DialogEditModel {
        b.btnCancel.setOnClickListener {
            onClick()
            cancel()
        }
        return this
    }

    fun show() = dialog.show()
    fun cancel() = dialog.cancel()

}
