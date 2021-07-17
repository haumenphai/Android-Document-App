package dotd.hmp.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dotd.hmp.R
import dotd.hmp.data.Field
import dotd.hmp.data.FieldType
import dotd.hmp.databinding.DialogGroupRecordsBinding
import dotd.hmp.databinding.ItemGroupByFieldBinding
import dotd.hmp.hepler.getStr

@SuppressLint("SetTextI18n")

class DialogPickFieldForGroup(
    private val context: Context,
    private val list: List<Field>
) {
    private val view by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_group_records, null) }
    private val b by lazy { DialogGroupRecordsBinding.bind(view) }
    private val dialog by lazy {
        Dialog(context).apply {
            setContentView(view)
        }
    }
    private val adapter by lazy {
        GroupRecordsAdapter(list)
    }

    init {
        b.recyclerView.layoutManager = LinearLayoutManager(context)
        b.recyclerView.adapter = adapter

    }

    fun setOnclickItem(onClick: (field: Field) -> Unit): DialogPickFieldForGroup {
        adapter.onClickItem = {
            onClick(it)
            adapter.unCheckAll()
            it.isChecked = true
            adapter.notifyDataSetChanged()
            cancel()
        }
        return this
    }

    fun unCheckAll() = adapter.unCheckAll()

    fun show(): DialogPickFieldForGroup {
        dialog.show()
        return this
    }

    fun cancel(): DialogPickFieldForGroup {
        dialog.cancel()
        return this
    }


    private class GroupRecordsAdapter(private var list: List<Field> = mutableListOf()):
        RecyclerView.Adapter<GroupRecordsAdapter.Holder>() {

        fun setList(list: List<Field>) {
            this.list = list
            notifyDataSetChanged()
        }

        fun unCheckAll() {
            list.forEach { it.isChecked = false }
            notifyDataSetChanged()
        }

        private fun getFieldTypesCanGroup(): List<FieldType> {
            return listOf(
                FieldType.DATETIME, FieldType.TEXT, FieldType.NUMBER
            )
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group_by_field, parent, false)
            return Holder(view)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val field = list[position]
            val b = holder.b

            if (field.fieldType !in getFieldTypesCanGroup()) {
                return
            }

            if (field.isChecked) {
                b.background.setBackgroundColor(Color.parseColor("#B6B6B6"))
            } else {
                b.background.setBackgroundResource(R.drawable.rippler_blue_white)
            }

            b.tv.text = "${getStr(R.string.group_by)}: ${field.fieldName}"

        }

        override fun getItemCount(): Int = list.size

        lateinit var onClickItem:(fileld: Field) -> Unit
        private inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val b = ItemGroupByFieldBinding.bind(itemView)
            init {
                b.background.setOnClickListener {
                    onClickItem(list[layoutPosition])
                }
            }
        }


    }
}

