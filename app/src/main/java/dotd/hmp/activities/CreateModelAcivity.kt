package dotd.hmp.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import dotd.hmp.R
import dotd.hmp.data.*
import dotd.hmp.databinding.ActivityCreateModelBinding
import  dotd.hmp.databinding.LayoutOneFieldBinding
import dotd.hmp.dialog.DialogConfirmCreateModel
import dotd.hmp.dialog.DialogShowMess
import dotd.hmp.hepler.UIHelper
import dotd.hmp.hepler.title
import dotd.hmp.hepler.toFieldNameShow
import dotd.hmp.hepler.toFieldNameStore

class CreateModelAcivity : AppCompatActivity() {
    private val b by lazy { ActivityCreateModelBinding.inflate(layoutInflater) }
    private val fieldList = mutableListOf<Field>()
    private val modelName by lazy { intent.getStringExtra("model_name")!! }
    private val pathIcon by lazy { intent.getStringExtra("path_icon")!! }
    private val fieldCount = MutableLiveData<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)
        b.layoutOneFieldDefault.root.visibility = View.GONE
        fieldList.clear()

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
                    showMess(getString(R.string.field_name_must_not_be_empty))
                    return@setOnClickListener
                }
                if (it.fieldName.isDefaultField()) {
                    showMess("${getString(R.string.model_must_not_contain___)}: " +
                            "$defaultField\n\n${getString(R.string.please_change)} \"${it.fieldName}\" " +
                            "${getString(R.string.to_other_name)}!")
                    return@setOnClickListener
                }
            }
            if (!checkFieldNameUnique(fieldList)) {
                showMess(getString(R.string.mess_field_name_not_unique))
                return@setOnClickListener
            }

            DialogConfirmCreateModel(this).apply {
                b.tvContent.text = getTextFieldsConfirm()
                b.tvModelName.text = modelName

                b.btnBack.setOnClickListener { dialog.cancel() }
                b.btnCreate.setOnClickListener {
                    val model = Model(modelName, pathIcon)
                    model.setFieldList(fieldList)
                    ModelDB.insert(model).also { isSuccess ->
                        if (!isSuccess) DialogShowMess.showMessInsertModelFailure(context)
                    }
                    finish()
                }
                dialog.show()
            }
        }
        b.scrollView.setOnTouchListener { v, event ->
            UIHelper.hideKeyboardFrom(this, b.root)
            false
        }

        fieldCount.observeForever {
            supportActionBar!!.title = "${getString(R.string.Create)} $modelName... ($it ${getString(R.string.field)})"
        }
    }


    private fun addNewLayoutOneFieldToRootLayout() {
        if (fieldCount.value != null) {
            fieldCount.postValue(fieldCount.value!! + 1)
        } else {
            fieldCount.postValue(1)
        }

        val layoutOneField = LayoutInflater.from(this).inflate(R.layout.layout_one_field, null)
        val b2 = LayoutOneFieldBinding.bind(layoutOneField)
        b.layoutContentScroll.addView(layoutOneField)

        val field = Field("", FieldType.TEXT)
        fieldList.add(field)

        b2.btnDeleteField.setOnClickListener {
            b.layoutContentScroll.removeView(layoutOneField)
            fieldList.remove(field)
            fieldCount.postValue(fieldCount.value!! - 1)
        }
    
        val listSpinner = listOf(getString(R.string.text), getString(R.string.number), getString(R.string.datetime))
        b2.spinner1.adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, listSpinner)

        b2.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (listSpinner[position]) {
                    getString(R.string.text) -> field.fieldType = FieldType.TEXT
                    getString(R.string.number) -> field.fieldType = FieldType.NUMBER
                    getString(R.string.datetime) -> field.fieldType = FieldType.DATETIME
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        b2.editFieldName.addTextChangedListener {
            field.fieldName = it.toString().toFieldNameStore()
        }

    }

    private fun showMess(mess: String) = AlertDialog.Builder(this).setMessage(mess).show()

    private fun getTextFieldsConfirm(): String {
        var textContent = ""
        fieldList.forEach {
            textContent += "${it.fieldName.toFieldNameShow()}: ${it.fieldType.toString().title()}\n"
        }
        return textContent
    }

    private fun checkFieldNameUnique(fieldList: List<Field>): Boolean {
        val fieldSet = fieldList.map { it.fieldName }.toSet()
        if (fieldList.size != fieldSet.size) {
            return false
        }
        return true
    }
}
