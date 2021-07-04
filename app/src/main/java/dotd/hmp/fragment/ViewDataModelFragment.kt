package dotd.hmp.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import dotd.hmp.R
import dotd.hmp.activities.ViewDataModelActivity
import dotd.hmp.adapter.DataModelAdpater
import dotd.hmp.data.Model
import dotd.hmp.databinding.FragmentViewDataModelBinding

class ViewDataModelFragment : Fragment() {
    private lateinit var b: FragmentViewDataModelBinding
    private val act: ViewDataModelActivity by lazy { activity as ViewDataModelActivity }
    private val model: MutableLiveData<Model> by lazy { act.model }
    private val adapter: DataModelAdpater = DataModelAdpater()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        b = FragmentViewDataModelBinding.inflate(inflater, container, false)
        hideSearchView()
        setConfigToolBar()
        setUpRecyclerView()

        return b.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_view_data_model_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> {
                act.addFragment(AddModelRecordFragment())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpRecyclerView() {
        model.observeForever {
            adapter.setModel(it)
        }

        b.recyclerView.layoutManager = LinearLayoutManager(act)
        b.recyclerView.adapter = adapter
        adapter.onClickItem = {
            Toast.makeText(act, it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setConfigToolBar() {
        act.setSupportActionBar(b.toolBar)
        b.appbarLayout.outlineProvider = null
        act.supportActionBar!!.title = model.value!!.name
    }

    private fun hideSearchView() {
        b.layoutSearch.visibility = View.GONE
    }

}
