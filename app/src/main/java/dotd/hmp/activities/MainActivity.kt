package dotd.hmp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import dotd.hmp.adapter.ModelApdater
import dotd.hmp.data.*
import dotd.hmp.databinding.ActivityMainBinding
import dotd.hmp.dialog.DialogAddNewModel
import dotd.hmp.hepler.UIHelper


class MainActivity : AppCompatActivity() {
    private val b by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val adapter: ModelApdater = ModelApdater()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)
        setUpRecyclerView()

        b.btnTest.setOnClickListener {
            // TODO: remove test
            val db = ModelDatabase.instance.dao()
            db.deleteAll()
        }

        b.btnInsert.setOnClickListener {
            // TODO: remove test
            ModelDB.insert(ModelDemoDatas.getModelStudent())
        }
    }

    fun setUpRecyclerView() {
        b.recyclerView.adapter = adapter
        b.recyclerView.layoutManager = GridLayoutManager(this, 4)

        ModelDatabase.instance.dao().getLiveData().observe(this, {
            val list = it.toMutableList()
            if (!list.contains(Model.itemAddNewModel))
                list.add(Model.itemAddNewModel)
            adapter.setList(list)

        })
        adapter.onClickItem = {
            if (it.isItemAddNewModel()) {
                DialogAddNewModel(this).apply {
                    setBtnOkClick { modelName, icon ->
                        UIHelper.hideKeyboardFrom(this@MainActivity, b.root)
                        val intent = Intent(this@MainActivity, CreateModelAcivity::class.java)
                        intent.putExtra("model_name", modelName)
                        intent.putExtra("icon", icon)
                        startActivity(intent)
                    }
                    setBtnCancelClick {
                        UIHelper.hideKeyboardFrom(this@MainActivity, b.root)
                    }
                    UIHelper.showKeyboard(this@MainActivity)
                    show()
                }
            } else {
                val intent = Intent(this, ViewDataModelActivity::class.java)
                intent.putExtra("model", it)
                startActivity(intent)
            }
        }
    }

}
