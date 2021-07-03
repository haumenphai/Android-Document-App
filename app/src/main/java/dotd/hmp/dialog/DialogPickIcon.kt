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
            ItemIcon(R.drawable.ic_baseline_add_24, ""),
            ItemIcon(R.drawable.ic_baseline_delete_24, ""),
            ItemIcon(R.drawable.ic_clear, ""),
        )
        adapter.setList(listIcon)
    }

    fun show() = dialog.show()

    fun cancel() = dialog.cancel()

    fun setItemIconClick(callBack: (itemIcon: ItemIcon) -> Unit): DialogPickIcon {
        adapter.onItemClick = {
            callBack(it)
        }
        return this
    }
}

class ItemIcon(var iconResource: Int, var name: String = "")

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

        b.imgIcon.setImageResource(itemIcon.iconResource)
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

