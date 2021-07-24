package dotd.hmp.hepler

import android.content.Context
import android.graphics.BitmapFactory
import android.os.CountDownTimer
import android.widget.ImageView
import dotd.hmp.MyApplication


fun ImageView.setImageAssets(path: String, context: Context = MyApplication.context) {
    this.setImageBitmap(BitmapFactory.decodeStream(context.assets.open(path)))
}


object TimeDelay {
    private var milisecond = 0L

    fun onFinish(onRun: () -> Unit) {
        object : CountDownTimer(milisecond, milisecond) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                onRun()
            }
        }.start()
    }

    fun setTime(milisecondDelay: Long): TimeDelay {
        this.milisecond = milisecondDelay
        return this
    }
}