package dotd.hmp.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import dotd.hmp.R
import dotd.hmp.databinding.ViewLoadingFullBinding

class DialogLoadingFullScreen(
    private val context: Context,
    private val viewRoot: ViewGroup
) {

    private val layoutLoading = LayoutInflater.from(context).inflate(R.layout.view_loading_full, null)
    private val b = ViewLoadingFullBinding.bind(layoutLoading)

    init {
        b.imgBg.setOnClickListener {  }
    }

    fun show() {
        viewRoot.addView(b.root)
    }

    fun cancel() {
        viewRoot.removeView(b.root)
    }

}