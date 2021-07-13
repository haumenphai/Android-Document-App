package dotd.hmp.dialog

import android.app.AlertDialog
import android.content.Context
import dotd.hmp.R
import dotd.hmp.hepler.getStr

class DialogShowMess {
    companion object {
        fun showMess(actContext: Context, mess: String) {
            AlertDialog.Builder(actContext)
                .setMessage(mess)
                .setNegativeButton("OK") {d,i ->}
                .show()
        }

        fun showMessInsertModelFailure(actContext: Context) {
            showMess(actContext, getStr(R.string.insert_model_failure))
        }

        fun showMessUpdateModelFailure(actContext: Context) {
            showMess(actContext, getStr(R.string.update_model_failure))
        }


    }
}