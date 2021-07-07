package dotd.hmp.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dotd.hmp.R
import dotd.hmp.activities.ViewRecordsActivity
import dotd.hmp.activities.ViewDetailRecordActivity
import dotd.hmp.adapter.RecordAdpater
import dotd.hmp.data.Model
import dotd.hmp.databinding.FragmentViewRecordsBinding

class ViewRecordsFragment : Fragment() {
    private lateinit var b: FragmentViewRecordsBinding
    private val act: ViewRecordsActivity by lazy { activity as ViewRecordsActivity }
    private val adapter: RecordAdpater = RecordAdpater()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        b = FragmentViewRecordsBinding.inflate(inflater, container, false)
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
                act.addFragment(AddRecordFragment())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpRecyclerView() {
        act.model.observeForever {
            adapter.setModel(it)
            b.tvNoRecord.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
        }

        b.recyclerView.layoutManager = LinearLayoutManager(act)
        b.recyclerView.adapter = adapter
        adapter.onClickItem = {
            val intent = Intent(context, ViewDetailRecordActivity::class.java)
            intent.putExtra("model", act.model.value)
            intent.putExtra("recordString", it.toString())
            startActivityForResult(intent, 123)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == RESULT_OK && data != null) {
            val model = data.getSerializableExtra("model") as Model
            act.model.value = model
        }
    }

    private fun setConfigToolBar() {
        act.setSupportActionBar(b.toolBar)
        b.appbarLayout.outlineProvider = null
        act.supportActionBar!!.title = act.model.value!!.name
    }

    private fun hideSearchView() {
        b.layoutSearch.visibility = View.GONE
    }

}
