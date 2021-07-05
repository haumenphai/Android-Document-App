package dotd.hmp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import dotd.hmp.R
import dotd.hmp.activities.ViewDataModelActivity
import dotd.hmp.data.FieldType
import dotd.hmp.data.ModelDB
import dotd.hmp.databinding.FieldDatetimeBinding
import dotd.hmp.databinding.FieldNumberBinding
import dotd.hmp.databinding.FragmentAddModelRecordBinding
import dotd.hmp.dialog.DialogPickDatetime
import dotd.hmp.hepler.UIHelper
import org.json.JSONObject


class AddModelRecordFragment: Fragment() {
    private lateinit var b: FragmentAddModelRecordBinding
    private val act: ViewDataModelActivity by lazy { activity as ViewDataModelActivity }
    private val jsonObj = org.json.JSONObject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentAddModelRecordBinding.inflate(inflater, container, false)
        setUpLayout()
        setClick()
        return b.root
    }

    @SuppressLint("SetTextI18n")
    fun setUpLayout() {
        val model = act.model.value!!
        model.getFieldList().forEach { field ->
            val jsonObject2 = JSONObject()

            when (field.fieldType) {
                FieldType.NUMBER -> {
                    val view = LayoutInflater.from(act).inflate(R.layout.field_number, null)
                    val binding = FieldNumberBinding.bind(view)
                    binding.tvFieldName.text = "${field.fieldName}:"
                    binding.editContent.addTextChangedListener { text ->
                        jsonObject2.put("fieldType", FieldType.NUMBER)
                        jsonObject2.put("value", text.toString())
                        jsonObj.put(field.fieldName, jsonObject2)
                    }
                    b.layoutField.addView(view)
                }
                FieldType.TEXT -> {
                    val view = LayoutInflater.from(act).inflate(R.layout.field_text, null)
                    val binding = FieldNumberBinding.bind(view)
                    binding.tvFieldName.text = "${field.fieldName}:"
                    binding.editContent.addTextChangedListener { text ->
                        jsonObject2.put("fieldType", FieldType.TEXT)
                        jsonObject2.put("value", text.toString())
                        jsonObj.put(field.fieldName, jsonObject2)
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
                            jsonObject2.put("fieldType", FieldType.DATETIME)
                            jsonObject2.put("value", dateTime.toMiliseconds())
                            jsonObj.put(field.fieldName, jsonObject2)
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
            model.addNewRecord(jsonObj)
            ModelDB.update(model)
            act.model.value = model

            act.removeFragment(this)
            UIHelper.hideKeyboardFrom(act, b.root)
        }
    }

}
