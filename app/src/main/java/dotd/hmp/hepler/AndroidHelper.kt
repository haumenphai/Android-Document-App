package dotd.hmp.hepler

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import dotd.hmp.MyApplication

fun ImageView.setImageAssets(path: String, context: Context = MyApplication.context) {
    this.setImageBitmap(BitmapFactory.decodeStream(context.assets.open(path)))
}