package dotd.hmp.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dotd.hmp.R
import dotd.hmp.data.Field
import dotd.hmp.databinding.DialogSortRecordsBinding
import dotd.hmp.databinding.ItemSortReocrdsBinding
import dotd.hmp.hepler.getStr
import dotd.hmp.hepler.toFieldNameShow

class DialogSortRecords(
    private val context: Context,
    private val list: List<Field>
) {
    private val view by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_sort_records, null) }
    private val b by lazy { DialogSortRecordsBinding.bind(view) }
    private val dialog by lazy {
        Dialog(context).apply {
            setContentView(view)
        }
    }
    private val adapter: FieldAdapter by lazy { FieldAdapter() }

    init {
        b.recyclerView.layoutManager = LinearLayoutManager(context)
        b.recyclerView.adapter = adapter
        adapter.setList(list)

    }

    fun setOnClickItem(onClick: (field: Field, reverse: Boolean) -> Unit): DialogSortRecords {
        adapter.onClickItem = {
            onClick(it, b.checkboxReverse.isChecked)
            adapter.unCheckAll()
            it.isChecked = true
            adapter.notifyDataSetChanged()
            cancel()
        }
        return this
    }

    fun show(): DialogSortRecords {
        dialog.show()
        return this
    }

    fun cancel(): DialogSortRecords {
        dialog.cancel()
        return this
    }


    private class FieldAdapter: RecyclerView.Adapter<FieldAdapter.Holder>() {
        private var list: List<Field> = mutableListOf()

        fun setList(list: List<Field>) {
            this.list = list
            notifyDataSetChanged()
        }

        fun getList() = list

        fun unCheckAll() {
            list.forEach { it.isChecked = false }
            notifyDataSetChanged()
        }



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sort_reocrds, parent, false)
            return Holder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: Holder, position: Int) {
            val field = list[position]
            val b = holder.b

            if (field.isChecked) {
                b.background.setBackgroundColor(Color.parseColor("#B6B6B6"))
            } else {
                b.background.setBackgroundResource(R.drawable.rippler_blue_white)
            }
            b.tvContent.text = "${getStr(R.string.sort_by)}: ${field.fieldName.toFieldNameShow()}"
        }

        override fun getItemCount(): Int = list.size




        lateinit var onClickItem: (field: Field) -> Unit
        private inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val b = ItemSortReocrdsBinding.bind(itemView)
            init {
                b.background.setOnClickListener {
                    onClickItem(list[layoutPosition])
                }
            }
        }


    }

}