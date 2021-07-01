package dotd.hmp.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import dotd.hmp.ModelApdater
import dotd.hmp.data.Model
import dotd.hmp.data.ModelDatabase
import dotd.hmp.databinding.ActivityMainBinding

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
                Toast.makeText(this, "open activity add new model", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, AddDataModelAcivity::class.java))
            } else {
                Toast.makeText(this, "open acitvity view data in model", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun Model.isItemAddNewModel(): Boolean = Model.itemAddNewModel == this
}