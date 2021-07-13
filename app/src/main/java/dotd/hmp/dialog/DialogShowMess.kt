package dotd.hmp.dialog

import android.app.AlertDialog
import dotd.hmp.MyApplication.Companion.context
import dotd.hmp.R
import dotd.hmp.hepler.getStr

class DialogShowMess {
    companion object {
        fun showMess(mess: String) {
            AlertDialog.Builder(context)
                .setMessage(mess)
                .setNegativeButton("OK") {d,i -> }
                .show()
        }

        fun showMessInsertModelFailure() {
            showMess(getStr(R.string.insert_model_failure))
        }

        fun showMessUpdateModelFailure() {
            showMess(getStr(R.string.update_model_failure))
        }


    }
}