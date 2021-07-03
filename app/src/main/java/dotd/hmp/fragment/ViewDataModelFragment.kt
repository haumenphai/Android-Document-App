package dotd.hmp.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import dotd.hmp.R
import dotd.hmp.activities.ViewDataModelActivity
import dotd.hmp.data.Model
import dotd.hmp.databinding.FragmentViewDataModelBinding

class ViewDataModelFragment : Fragment() {
    private lateinit var b: FragmentViewDataModelBinding
    private val act: ViewDataModelActivity by lazy { activity as ViewDataModelActivity }
    private val model: Model by lazy { act.model }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        b = FragmentViewDataModelBinding.inflate(inflater, container, false)
        hideSearchView()
        setConfigToolBar()

        //todo: remove test
        Log.d("CCC", model.getJsonArray().toString())

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

    private fun setConfigToolBar() {
        act.setSupportActionBar(b.toolBar)
        b.appbarLayout.outlineProvider = null
        act.supportActionBar!!.title = model.name
    }

    fun hideSearchView() {
        b.layoutSearch.visibility = View.GONE
    }

}
