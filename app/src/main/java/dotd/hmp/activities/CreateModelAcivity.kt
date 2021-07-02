package dotd.hmp.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import dotd.hmp.R
import dotd.hmp.data.Field
import dotd.hmp.data.FieldType
import dotd.hmp.data.Model
import dotd.hmp.data.ModelDB
import dotd.hmp.databinding.ActivityCreateModelBinding
import  dotd.hmp.databinding.LayoutOneFieldBinding
import dotd.hmp.dialog.DialogConfirmCreateModel
import dotd.hmp.hepler.UIHelper
import dotd.hmp.hepler.title

class CreateModelAcivity : AppCompatActivity() {
    private val b by lazy { ActivityCreateModelBinding.inflate(layoutInflater) }
    private val fieldList = mutableListOf<Field>()

    private val modelName by lazy { intent.getStringExtra("model_name")!! }
    private val modelColor by lazy { intent.getIntExtra("color", -1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)
        b.layoutOneFieldDefault.root.visibility = View.GONE
        fieldList.clear()
        supportActionBar!!.title = modelName

        addNewLayoutOneFieldToRootLayout()
        setClick()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClick() {
        b.btnAddNewField.setOnClickListener {
            addNewLayoutOneFieldToRootLayout()
        }

        b.btnCreate.setOnClickListener {
            fieldList.forEach {
                if (!it.isValid()) {
                    showMess("Field Name mustn't be empty")
                    return@setOnClickListener
                }
            }
            DialogConfirmCreateModel(this).apply {
                b.tvContent.setText(getTextFieldsConfirm())
                b.tvModelName.setText(modelName)

                b.btnBack.setOnClickListener { dialog.cancel() }
                b.btnCreate.setOnClickListener {
                    val model = Model(modelName, modelColor)
                    model.setFieldList(fieldList)
                    ModelDB.insert(model)
                    finish()
                }
                dialog.show()
            }
        }
        b.scrollView.setOnTouchListener { v, event ->
            UIHelper.hideKeyboardFrom(this, b.root)
            false
        }
    }



    private fun addNewLayoutOneFieldToRootLayout() {
        val layoutOneField = LayoutInflater.from(this).inflate(R.layout.layout_one_field, null)
        val b2 = LayoutOneFieldBinding.bind(layoutOneField)
        b.layoutContentScroll.addView(layoutOneField)

        val field = Field("", FieldType.TEXT)
        fieldList.add(field)

        b2.btnDeleteField.setOnClickListener {
            b.layoutContentScroll.removeView(layoutOneField)
            fieldList.remove(field)
        }
    
        val listSpinner = listOf("Text", "Number")
        b2.spinner1.adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, listSpinner)

        b2.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (listSpinner[position]) {
                    "Text" -> field.fieldType = FieldType.TEXT
                    "Number" -> field.fieldType = FieldType.NUMBER
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        b2.editFieldName.addTextChangedListener {
            field.fieldName = it.toString()
        }

    }

    private fun showMess(mess: String) = AlertDialog.Builder(this).setMessage(mess).show()

    private fun getTextFieldsConfirm(): String {
        var textContent = ""
        fieldList.forEach {
            textContent += "${it.fieldName}: ${it.fieldType.toString().title()}\n"
        }
        return textContent
    }

    //   b2.editFieldName.inputType = InputType.TYPE_CLASS_TEXT +
    //                                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS

    //                         b2.editFieldName.inputType = InputType.TYPE_CLASS_NUMBER
}