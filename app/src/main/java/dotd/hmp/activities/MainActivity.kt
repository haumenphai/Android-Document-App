package dotd.hmp.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import dotd.hmp.ModelApdater
import dotd.hmp.data.Model
import dotd.hmp.data.ModelDatabase
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
            ModelDatabase.instance.dao().insert(
                Model("Student", Color.GREEN),
                Model("Employee", Color.RED)
            )
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
                    setBtnOkClick { modelName, color ->
                        UIHelper.hideKeyboardFrom(this@MainActivity, b.root)
                        val intent = Intent(this@MainActivity, CreateModelAcivity::class.java)
                        intent.putExtra("model_name", modelName)
                        intent.putExtra("color", color)
                        startActivity(intent)
                    }
                    setBtnCancelClick {
                        UIHelper.hideKeyboardFrom(this@MainActivity, b.root)
                    }
                    UIHelper.showKeyboard(this@MainActivity)
                    show()
                }
            } else {
                // open acitvity view data in model
            }
        }
    }

    fun Model.isItemAddNewModel(): Boolean = Model.itemAddNewModel == this
}