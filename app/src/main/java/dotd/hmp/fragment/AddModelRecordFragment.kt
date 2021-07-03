package dotd.hmp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import dotd.hmp.R
import dotd.hmp.activities.ViewDataModelActivity
import dotd.hmp.data.FieldType
import dotd.hmp.data.Model
import dotd.hmp.data.ModelDB
import dotd.hmp.databinding.FieldNumberBinding
import dotd.hmp.databinding.FragmentAddModelRecordBinding

class AddModelRecordFragment: Fragment() {
    private lateinit var b: FragmentAddModelRecordBinding
    private val act: ViewDataModelActivity by lazy { activity as ViewDataModelActivity }
    private val model: Model by lazy { act.model }
    private val jsonObj = org.json.JSONObject()


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

    fun setUpLayout() {
        model.getFieldList().forEach { field ->
            when (field.fieldType) {
                FieldType.NUMBER -> {
                    val view  = LayoutInflater.from(act).inflate(R.layout.field_number, null)
                    val binding = FieldNumberBinding.bind(view)
                    binding.tvFieldName.text = "${field.fieldName}:"
                    binding.editContent.addTextChangedListener { text ->
                        jsonObj.put(field.fieldName, text.toString())
                    }
                    b.root.addView(view)
                }
                FieldType.TEXT -> {
                    val view  = LayoutInflater.from(act).inflate(R.layout.field_text, null)
                    val binding = FieldNumberBinding.bind(view)
                    binding.tvFieldName.text = "${field.fieldName}:"
                    binding.editContent.addTextChangedListener { text ->
                        jsonObj.put(field.fieldName, text.toString())
                    }
                    b.root.addView(view)
                }
            }
        }
    }

    fun setClick() {
        b.btnCreateRecord.setOnClickListener {
            model.addNewRecord(jsonObj)
            ModelDB.update(model)
            act.removeFragment(this)
        }
    }


}
