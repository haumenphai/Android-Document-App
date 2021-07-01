package dotd.hmp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import dotd.hmp.R
import  dotd.hmp.databinding.LayoutOneFieldBinding
import dotd.hmp.databinding.ActivityAddDataModelBinding

class AddDataModelAcivity : AppCompatActivity() {
    private val b by lazy { ActivityAddDataModelBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)
        b.layoutOneFieldDefault.root.visibility = View.GONE
        addNewLayoutOneFieldToRootLayout()

        b.btnAddNewField.setOnClickListener {
            addNewLayoutOneFieldToRootLayout()
        }

        b.btnCreate.setOnClickListener {

        }


    }

    private fun addNewLayoutOneFieldToRootLayout() {
        val layoutOneField = LayoutInflater.from(this).inflate(R.layout.layout_one_field, null)
        val b2 = LayoutOneFieldBinding.bind(layoutOneField)
        b.layoutContentScroll.addView(layoutOneField)

        b2.btnDeleteField.setOnClickListener {
            Toast.makeText(this, "delete ${b2.editFieldName.text}", Toast.LENGTH_SHORT).show()
        }

        b2.spinner1.adapter = getAdapterSpiner()
    }

    private fun getAdapterSpiner() = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item,
        listOf("Text", "Number")
    )

}