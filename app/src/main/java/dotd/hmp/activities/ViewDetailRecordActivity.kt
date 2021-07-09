package dotd.hmp.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.gson.Gson
import com.google.gson.JsonObject
import dotd.hmp.R
import dotd.hmp.data.FieldType
import dotd.hmp.data.Model
import dotd.hmp.data.ModelDB
import dotd.hmp.data.getValueOfField
import dotd.hmp.databinding.ActivityViewDetailRecordBinding
import dotd.hmp.databinding.FieldDatetimeBinding
import dotd.hmp.databinding.FieldNumberBinding
import dotd.hmp.dialog.DialogPickDatetime
import dotd.hmp.hepler.DateTimeHelper
import dotd.hmp.hepler.UIHelper
import dotd.hmp.hepler.toFieldNameShow
import dotd.hmp.hepler.setTextHTML
import java.util.*

class ViewDetailRecordActivity : AppCompatActivity() {
    private val b by lazy { ActivityViewDetailRecordBinding.inflate(layoutInflater) }
    private val model: Model by lazy { intent.getSerializableExtra("model") as Model }
    private val recordString by lazy { intent.getStringExtra("recordString") }
    private val record: JsonObject by lazy { Gson().fromJson(recordString, JsonObject::class.java) }
    private val recordCopy: JsonObject by lazy { record.deepCopy() }
    private val viewsToRemove = mutableListOf<View>()

    companion object {
        val ACTION_EDIT_RECORD = "action edit record"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)

        setUpLayoutViewRecord(record)
        setUpLayoutEditRecord()
        setClick()
        handleIntentAction()
    }

    private fun handleIntentAction() {
        intent.action?.let { action ->
            when(action) {
                ACTION_EDIT_RECORD -> {
                    hideLayoutViewData()
                    showLayoutEditData()
                }
            }
        }
    }

    private fun setClick() {
        b.btnEdit.setOnClickListener {
            hideLayoutViewData()
            showLayoutEditData()
        }
        b.btnSave.setOnClickListener {
            UIHelper.hideKeyboardFrom(this, b.root)
            model.updateRecord(recordCopy)
            ModelDB.update(model)
            setResultForViewRecordsFragment()
            setUpLayoutViewRecord(recordCopy)
            hideLayoutEditData()
            showLayoutViewData()
        }
        b.btnCancel.setOnClickListener {
            hideLayoutEditData()
            showLayoutViewData()
        }
        b.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Record")
                .setMessage("Delete Record?")
                .setPositiveButton("Delete") { t ,l ->
                    model.deleteRecord(record)
                    ModelDB.update(model)
                    setResultForViewRecordsFragment()
                    finish()
                }
                .setNegativeButton("Cancel") { t, l ->

                }.show()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setUpLayoutEditRecord() {
        model.getFieldList().forEach { field ->
            val jsonObj = record.get(field.fieldName).asJsonObject

            when(field.fieldType) {
                FieldType.NUMBER -> {
                    val view = LayoutInflater.from(this).inflate(R.layout.field_number, null)
                    val binding = FieldNumberBinding.bind(view)
                    binding.tvFieldName.text = "${field.fieldName}:"
                    binding.editContent.setText(record.getValueOfField(field.fieldName))
                    binding.editContent.addTextChangedListener { text ->
                        jsonObj.addProperty("value", text.toString())
                    }
                    b.layoutEditData.addView(view)
                }
                FieldType.TEXT -> {
                    val view = LayoutInflater.from(this).inflate(R.layout.field_text, null)
                    val binding = FieldNumberBinding.bind(view)
                    binding.tvFieldName.text = "${field.fieldName}:"
                    binding.editContent.setText(record.getValueOfField(field.fieldName))
                    binding.editContent.addTextChangedListener { text ->
                        jsonObj.addProperty("value", text.toString())
                    }
                    b.layoutEditData.addView(view)
                }
                FieldType.DATETIME -> {
                    val view = LayoutInflater.from(this).inflate(R.layout.field_datetime, null)
                    val binding = FieldDatetimeBinding.bind(view)
                    binding.tvFieldName.text = "${field.fieldName}:"
                    binding.editDatePreview.setText(DateTimeHelper.timestampToDatetimeString(record.getValueOfField(field.fieldName).toLong()))
                    binding.btnPickDateTime.setOnClickListener {
                        DialogPickDatetime.show(it.context) { dateTime ->
                            binding.editDatePreview.setText(dateTime.format())
                            jsonObj.addProperty("value", dateTime.toMiliseconds().toString())
                        }
                    }
                    b.layoutEditData.addView(view)
                }
            }
            recordCopy.add(field.fieldName, jsonObj)
        }
    }


    private fun setUpLayoutViewRecord(record: JsonObject) {
        var text = ""
        val fieldNames = record.keySet()
        for (fieldName in fieldNames) {
            if (fieldName == "id" || fieldName == "is_selected") continue

            val fieldType = record.get(fieldName).asJsonObject.get("fieldType").asString
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

    private fun setResultForViewRecordsFragment() {
        val intent = Intent()
        intent.putExtra("model", model)
        setResult(RESULT_OK, intent)
    }

    private fun hideLayoutViewData() {
        b.layoutViewData.visibility = View.GONE
        b.layoutBtn1.visibility = View.INVISIBLE
    }

    private fun hideLayoutEditData() {
        b.layoutEditData.visibility = View.GONE
        b.layoutBtn2.visibility = View.INVISIBLE
    }

    private fun showLayoutViewData() {
        b.layoutViewData.visibility = View.VISIBLE
        b.layoutBtn1.visibility = View.VISIBLE
    }

    private fun showLayoutEditData() {
        b.layoutEditData.visibility = View.VISIBLE
        b.layoutBtn2.visibility = View.VISIBLE
    }
}
