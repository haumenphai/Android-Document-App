package dotd.hmp.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import dotd.hmp.R
import dotd.hmp.data.*
import dotd.hmp.databinding.ItemDataModelBinding
import dotd.hmp.hepler.DateTimeHelper
import dotd.hmp.hepler.toFieldNameShow
import dotd.hmp.hepler.setTextHTML
import java.lang.Exception

class RecordAdapater :
        RecyclerView.Adapter<RecordAdapater.DataModelHolder>() {

    private lateinit var recordList: List<JsonObject>
    private lateinit var model: Model
    var startIndex: Int = 0

    @JvmName("setModel1")
    fun setModel(model: Model) {
        this.model = model
        this.recordList = model.getRecordList()
        notifyDataSetChanged()
    }

    fun setRecordList(list: List<JsonObject>) {
        this.recordList = list
        notifyDataSetChanged()
    }

    @JvmName("setStartIndex1")
    fun setStartIndex(start: Int) {
        this.startIndex = start
        notifyDataSetChanged()
    }


    fun getModel() = model

    fun unSelectAll() {
        recordList.forEach { it.remove("is_selected") }
        notifyDataSetChanged()
    }

    fun selectAll() {
        recordList.forEach { it.addProperty("is_selected", true) }
        notifyDataSetChanged()
    }

    fun getRecordsSeleted(): List<JsonObject> {
         return recordList.filter {
            var isSelect = false
            try { isSelect = it.get("is_selected").asBoolean } catch (e: Exception) {}
            isSelect
         }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataModelHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_data_model, parent, false)
        return DataModelHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DataModelHolder, position: Int) {
        val b = holder.b
        val record = recordList[position]
        b.tvSequence.text = "#${startIndex+position+1}"

        try {
            if (record.get("is_selected").asBoolean) {
                b.background.setBackgroundColor(Color.parseColor("#ffd4df"))
            } else {
                b.background.setBackgroundResource(R.drawable.rippler_blue_white)
            }
        } catch (e: Exception) {
            b.background.setBackgroundResource(R.drawable.rippler_blue_white)
        }


        var text = ""
        val fieldNames = record.keySet()

        for (fieldName in fieldNames) {
            // hide default field in list view
            if (fieldName.isDefaultField() || fieldName == "is_selected") continue


            val fieldType = record.getFieldType(fieldName)
            val value =  record.getValueOfField(fieldName)
            when (fieldType) {
                FieldType.TEXT.toString(), FieldType.NUMBER.toString() -> {
                    text += """
                        <font color="black">
                           <b>${fieldName.toFieldNameShow()}:</b>
                        </font> $value <br/>
                    """.trimIndent()
                }
                FieldType.DATETIME.toString() -> {
                    text += """
                        <font color="black">
                           <b>${fieldName.toFieldNameShow()}:</b>
                        </font> ${DateTimeHelper.timestampToDatetimeString(value.toLong())} <br/>
                    """.trimIndent()
                }
            }
        }
        b.tvData.setTextHTML(text)
    }



    override fun getItemCount(): Int = recordList.size

    lateinit var onClickItem: (record: JsonObject) -> Unit
    lateinit var onLongClickItem: (record: JsonObject) -> Unit

    inner class DataModelHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val b = ItemDataModelBinding.bind(itemView)
        init {
            b.background.setOnClickListener {
                onClickItem(recordList[layoutPosition])
            }
            b.background.setOnLongClickListener {
                onLongClickItem(recordList[layoutPosition])
                true
            }
        }
    }

}