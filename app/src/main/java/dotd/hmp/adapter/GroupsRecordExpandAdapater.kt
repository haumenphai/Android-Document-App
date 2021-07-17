package dotd.hmp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.google.gson.JsonObject
import dotd.hmp.R
import dotd.hmp.data.*
import dotd.hmp.databinding.ItemGroupByFieldBinding
import dotd.hmp.databinding.ItemRecordsInGroupsBinding
import dotd.hmp.hepler.DateTimeHelper
import dotd.hmp.hepler.setTextHTML
import dotd.hmp.hepler.toFieldNameShow
import java.lang.Exception

@SuppressLint("SetTextI18n")
class GroupsRecordExpandAdapater: BaseExpandableListAdapter {
    private var context: Context
    private var titleList: List<String> = listOf()
    private var detailMap: HashMap<String, List<JsonObject>> = hashMapOf()
    private lateinit var grbField: Field


    constructor(context: Context) {
        this.context = context
    }

    constructor(context: Context, listTitle: List<String>, subList: HashMap<String, List<JsonObject>>, grbField: Field) {
        this.context = context
        this.titleList = listTitle
        this.detailMap = subList
        this.grbField = grbField
    }

    fun setData(context: Context, listTitle: List<String>, subList: HashMap<String, List<JsonObject>>, grbField: Field) {
        this.context = context
        this.titleList = listTitle
        this.detailMap = subList
        this.grbField = grbField
        notifyDataSetChanged()
    }

    fun unSelectAll() {
        detailMap.keys.forEach {
            val list = detailMap.get(it)
            list!!.forEach {
                it.remove("is_selected")
            }
        }
        notifyDataSetChanged()
    }

    fun selectAll() {
        detailMap.keys.forEach {
            val list = detailMap.get(it)
            list!!.forEach {
                it.addProperty("is_selected", true)
            }
        }
        notifyDataSetChanged()
    }

    fun getRecordsSelected(): List<JsonObject> {
        val result = mutableListOf<JsonObject>()
        detailMap.keys.forEach {
            val list = detailMap.get(it)

            result.addAll(list!!.filter {
                var isSelect = false
                try { isSelect = it.get("is_selected").asBoolean } catch (e: Exception) {}
                isSelect
            })
        }
        return result
    }


    override fun getGroupCount(): Int = titleList.size

    override fun getChildrenCount(groupPosition: Int): Int {
        val child = detailMap[titleList[groupPosition]]
        if (child != null) {
            return child.size
        }
        return 0
    }

    override fun getGroup(groupPosition: Int): Any = titleList[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Any? {
        val childList = detailMap[titleList[groupPosition]]
        if (childList != null) {
            return childList[childPosition]
        }

        return null
    }

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = false

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true



    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val titleGroup = getGroup(groupPosition) as String
        val view: View
        val b: ItemGroupByFieldBinding
        if (convertView == null) {
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.item_group_by_field, null)
            b = ItemGroupByFieldBinding.bind(view)
        } else {
            view = convertView
            b = ItemGroupByFieldBinding.bind(convertView)
        }

        b.tv.text = "${grbField.fieldName}: $titleGroup (${getChildrenCount(groupPosition)})"

        return view
    }


    var onClickItem: (record: JsonObject) -> Unit = {}
    var onLongClickItem: (record: JsonObject) -> Unit = {}

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val b: ItemRecordsInGroupsBinding
        val view: View

        if (convertView == null) {
            val layoutInflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.item_records_in_groups, null)
            b = ItemRecordsInGroupsBinding.bind(view)
        } else {
            b = ItemRecordsInGroupsBinding.bind(convertView)
            view = convertView
        }
        val record: JsonObject
        try {
            record = getChild(groupPosition, childPosition) as JsonObject
        } catch (e: Exception) {
            return view
        }


        b.background.setOnClickListener {
            onClickItem(record)
        }
        b.background.setOnLongClickListener {
            onLongClickItem(record)
            true
        }


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
        b.tvSequence.text = "#${childPosition+1}"
        return view
    }


}