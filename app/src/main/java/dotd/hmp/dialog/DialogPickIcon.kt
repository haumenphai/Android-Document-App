package dotd.hmp.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dotd.hmp.R
import dotd.hmp.databinding.DialogPickIconBinding
import dotd.hmp.databinding.ItemIconBinding
import dotd.hmp.hepler.setImageAssets

class DialogPickIcon(val context: Context) {
    private val view by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_pick_icon, null) }
    val b by lazy { DialogPickIconBinding.bind(view) }
    val dialog by lazy { Dialog(context) }

    private val adapter = ItemIconApdapter()

    init {
        dialog.setContentView(view)
        b.recyclerView.layoutManager = GridLayoutManager(context, 4)
        b.recyclerView.adapter = adapter

        val listIcon = listOf(
            // todo: this is demo data, remove it
            ItemIcon("auto_tone.png", ""),
            ItemIcon("icon_trai_tim.png", ""),
            ItemIcon("icon_trai_tim_xam.png", ""),
        )
        adapter.setList(listIcon)
    }

    fun show() = dialog.show()

    fun cancel() = dialog.cancel()

    fun setItemIconClick(callBack: (itemIcon: ItemIcon) -> Unit): DialogPickIcon {
        adapter.onItemClick = {
            callBack(it)
            cancel()
        }
        return this
    }
}

class ItemIcon(var pathIcon: String, var name: String = "")

class ItemIconApdapter(private var list: List<ItemIcon> = mutableListOf()):
    RecyclerView.Adapter<ItemIconApdapter.ItemIconHolder>() {

    @JvmName("setList1")
    fun setList(list: List<ItemIcon>) {
        this.list = list
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemIconHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_icon, parent, false)
        return ItemIconHolder(view)
    }

    override fun onBindViewHolder(holder: ItemIconHolder, position: Int) {
        val b = holder.b
        val itemIcon = list[position]

        b.imgIcon.setImageAssets(itemIcon.pathIcon)
    }

    override fun getItemCount(): Int = list.size

    lateinit var onItemClick: (itemIcon: ItemIcon) -> Unit
    inner class ItemIconHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val b = ItemIconBinding.bind(itemView)
        init {
            b.imgIcon.setOnClickListener {
                onItemClick(list[layoutPosition])
            }
        }
    }

}

