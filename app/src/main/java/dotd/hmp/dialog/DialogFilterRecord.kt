package dotd.hmp.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dotd.hmp.R
import dotd.hmp.data.*
import dotd.hmp.databinding.DialogFilterRecordsBinding
import dotd.hmp.databinding.ItemFilterRecordBinding
import dotd.hmp.hepler.*

class DialogFilterRecord(
    private val context: Context,
    private val model: Model
) {
    private val view by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_filter_records, null) }
    private val b by lazy { DialogFilterRecordsBinding.bind(view) }
    private val dialog by lazy { Dialog(context) }
    private val adapter: FilterRecordAdapter by lazy {
        FilterRecordAdapter()
    }
    private var filterRecordList = mutableListOf<FilterRecord>()


    init {
        dialog.setContentView(view)
        filterRecordList = FilterRecordDB.getList(model.id).toMutableList()
        adapter.setList(filterRecordList)


        val fieldNameList = model.getFieldList().map { it.fieldName.toFieldNameShow() }.toMutableList()
        val operatorListShow = FilterRecord.operatorAvailable.values.toMutableList()

        var field: Field = model.getField(fieldNameList[0].toFieldNameStore())!!
        var operator: String = FilterRecord.operatorAvailable.getKey(operatorListShow[0])!!
        var value: String

        b.recyclerView.layoutManager = LinearLayoutManager(context)
        b.recyclerView.adapter = adapter

        b.spinnerFieldName.adapter =
            ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, fieldNameList)
        b.spinnerFieldName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                field = model.getField(fieldNameList[position].toFieldNameStore())!!
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        b.spinnerOperator.adapter =
            ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, operatorListShow)
        b.spinnerOperator.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                operator = FilterRecord.operatorAvailable.getKey(operatorListShow[position])!!
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        b.btnAddNewFilter.setOnClickListener {
            value = b.editValue.text.toString().trim()
            if (value.isEmpty()) {
                DialogShowMess.showMess(context, "Value mustn't be empty!")
                return@setOnClickListener
            }

            val filterRecord = FilterRecord().apply {
                this.modelId = model.id
                this.setFiled(field)
                this.operator = operator
                this.value = value
            }

            filterRecordList.add(filterRecord)
            adapter.setList(filterRecordList)
            FilterRecordDB.insert(filterRecord)
        }

        adapter.onBtnDeleteClick = { filterRecord ->
            DialogConfirm(context)
                .setTitle("Delete Filter")
                .setMess("Delete Filter: $filterRecord?")
                .setTextBtnOk(getStr(R.string.delete))
                .setTextBtnCancel(getStr(R.string.cancel))
                .setBtnOkClick {
                    FilterRecordDB.delete(filterRecord)
                    filterRecordList.remove(filterRecord)
                    adapter.setList(filterRecordList)
                }.setBtnCancelClick {

                }.show()
        }

    }

    fun show(): DialogFilterRecord {
        dialog.show()
        return this
    }

    fun cancel(): DialogFilterRecord {
        dialog.cancel()
        return this
    }

    fun setClickItem(onClick:(filterRecord: FilterRecord) -> Unit): DialogFilterRecord {
        adapter.onClickItem = {
            adapter.unCheckAll()
            it.isChecked = true
            adapter.notifyDataSetChanged()
            onClick(it)
            cancel()
        }
        return this
    }

    fun setOnLongClickItem(onLongClick: (filterRecord: FilterRecord) -> Unit): DialogFilterRecord {
        adapter.onLongClickItem = {

            onLongClick(it)
        }
        return this
    }

    fun unCheckAll() = adapter.unCheckAll()




    private class FilterRecordAdapter: RecyclerView.Adapter<FilterRecordAdapter.Holder>() {
        private var list: List<FilterRecord> = mutableListOf()

        fun setList(list: List<FilterRecord>) {
            this.list = list
            notifyDataSetChanged()
        }

        fun unCheckAll() {
            list.forEach { it.isChecked = false }
        }



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_filter_record, parent, false)
            return Holder(view)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val filterRecord = list[position]
            val b = holder.b

            if (filterRecord.isChecked) {
                b.background.setBackgroundColor(Color.parseColor("#B6B6B6"))
            } else {
                b.background.setBackgroundResource(R.drawable.rippler_blue_white)
            }

            val text = """
                ${filterRecord.getField().fieldName.toFieldNameShow()} <b>${filterRecord.getOperatorToShow()}</b> ${filterRecord.value}
            """.trimIndent()
            b.tvContent.setTextHTML(text)
        }

        override fun getItemCount(): Int = list.size


        var onClickItem: (filter: FilterRecord) -> Unit = {}
        var onLongClickItem: (filter: FilterRecord) -> Unit = {}
        var onBtnDeleteClick: (filter: FilterRecord) -> Unit = {}

        private inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val b = ItemFilterRecordBinding.bind(itemView)
            init {
                b.background.setOnClickListener {
                    onClickItem(list[layoutPosition])
                }
                b.background.setOnLongClickListener {
                    onLongClickItem(list[layoutPosition])
                    true
                }
                b.imgDelete.setOnClickListener {
                    onBtnDeleteClick(list[layoutPosition])
                }
            }
        }


    }
}