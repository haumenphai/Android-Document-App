package dotd.hmp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import dotd.hmp.data.ModelDatabase
import dotd.hmp.data.ModelDemoDatas
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
            db.insert(Model("Do", 1))
            Log.d("CCC", db.getList()[0].name)
            Toast.makeText(this, db.getList()[0].name, Toast.LENGTH_SHORT).show()

            db.deleteAll()
        }
    }

    fun setUpRecyclerView() {

        b.recyclerView.adapter = adapter
        b.recyclerView.layoutManager = GridLayoutManager(this, 4)
        // TODO: remove demo add livedata
        adapter.setList(ModelDemoDatas.getDemoDatas())
        adapter.onClickItem = { model ->
            Toast.makeText(this, model.name, Toast.LENGTH_SHORT).show()
        }
    }
}