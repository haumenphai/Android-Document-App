package dotd.hmp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import dotd.hmp.R
import dotd.hmp.activities.ViewRecordsActivity
import dotd.hmp.data.FieldType
import dotd.hmp.data.ModelDB
import dotd.hmp.databinding.FieldDatetimeBinding
import dotd.hmp.databinding.FieldNumberBinding
import dotd.hmp.databinding.FragmentAddModelRecordBinding
import dotd.hmp.dialog.DialogPickDatetime
import dotd.hmp.hepler.UIHelper


class AddRecordFragment: Fragment() {
    private lateinit var b: FragmentAddModelRecordBinding
    private val act: ViewRecordsActivity by lazy { activity as ViewRecordsActivity }
    private val jsonObj = JsonObject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        b = FragmentAddModelRecordBinding.inflate(inflater, container, false)
        setUpLayout()
        setClick()
        return b.root
    }

    @SuppressLint("SetTextI18n")
    fun setUpLayout() {
        val model = act.model.value!!
        model.getFieldList().forEach { field ->
            val jsonObject2 = JsonObject()
            jsonObject2.addProperty("fieldType", field.fieldType.toString())
            jsonObject2.addProperty("value", "")
            jsonObj.add(field.fieldName, jsonObject2)

            when (field.fieldType) {
                FieldType.NUMBER -> {
                    val view = LayoutInflater.from(act).inflate(R.layout.field_number, null)
                    val binding = FieldNumberBinding.bind(view)
                    binding.tvFieldName.text = "${field.fieldName}:"


                    binding.editContent.addTextChangedListener { text ->
                        jsonObject2.addProperty("value", text.toString())
                        jsonObj.add(field.fieldName, jsonObject2)
                    }
                    b.layoutField.addView(view)
                }
                FieldType.TEXT -> {
                    val view = LayoutInflater.from(act).inflate(R.layout.field_text, null)
                    val binding = FieldNumberBinding.bind(view)
                    binding.tvFieldName.text = "${field.fieldName}:"

                    binding.editContent.addTextChangedListener { text ->
                        jsonObject2.addProperty("value", text.toString())
                        jsonObj.add(field.fieldName, jsonObject2)
                    }
                    b.layoutField.addView(view)
                }
                FieldType.DATETIME -> {
                    val view = LayoutInflater.from(act).inflate(R.layout.field_datetime, null)
                    val binding = FieldDatetimeBinding.bind(view)
                    binding.tvFieldName.text = "${field.fieldName}:"

                    binding.btnPickDateTime.setOnClickListener {
                        DialogPickDatetime.show(it.context) { dateTime ->
                            binding.editDatePreview.setText(dateTime.format())
                            jsonObject2.addProperty("value", dateTime.toMiliseconds().toString())
                            jsonObj.add(field.fieldName, jsonObject2)
                        }
                    }
                    b.layoutField.addView(view)
                }
            }
        }
    }

    fun setClick() {
        b.btnCreateRecord.setOnClickListener {
            val model = act.model.value!!
            model.addRecord(jsonObj)
            ModelDB.update(model)
            act.model.value = model

            act.removeFragment(this)
            UIHelper.hideKeyboardFrom(act, b.root)
        }
    }

}
