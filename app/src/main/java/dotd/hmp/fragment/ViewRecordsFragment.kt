package dotd.hmp.fragment

import android.annotation.SuppressLint
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
import dotd.hmp.adapter.RecordAdapater
import dotd.hmp.data.Model
import dotd.hmp.data.ModelDB
import dotd.hmp.databinding.FragmentViewRecordsBinding
import dotd.hmp.dialog.DialogConfirm
import dotd.hmp.hepler.getStr
import java.lang.Exception

class ViewRecordsFragment : Fragment() {
    private lateinit var b: FragmentViewRecordsBinding
    private val act: ViewRecordsActivity by lazy { activity as ViewRecordsActivity }
    private val adapter: RecordAdapater = RecordAdapater()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        b = FragmentViewRecordsBinding.inflate(inflater, container, false)
        hideView()
        setConfigToolBar()
        setUpRecyclerView()
        setUpLayoutAction()

        return b.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_view_data_model_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> {
                act.addFragment(AddRecordFragment(), "add_record_fragment")
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
        setClickRecord()
    }

    private fun setClickRecord() {
        fun hideActionEdit() {
            if (adapter.getRecordsSeleted().size != 1) {
                b.layoutActionRecords.actionEdit.visibility = View.INVISIBLE
            } else {
                b.layoutActionRecords.actionEdit.visibility = View.VISIBLE
            }
        }

        adapter.onClickItem = {
            val intent = Intent(context, ViewDetailRecordActivity::class.java)
            intent.putExtra("model", act.model.value)
            intent.putExtra("recordString", it.toString())
            startActivityForResult(intent, 123)
        }
        adapter.onLongClickItem = {
            b.layoutActionRecords.root.visibility = View.VISIBLE
            it.addProperty("is_selected", true)
            hideActionEdit()

            adapter.onClickItem = {
                try {
                    val isSelected = it.get("is_selected").asBoolean
                    it.addProperty("is_selected", !isSelected)
                } catch (e: Exception) {
                    it.addProperty("is_selected", true)
                }
                adapter.notifyDataSetChanged()
                hideActionEdit()
            }
            adapter.notifyDataSetChanged()
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

    @SuppressLint("SetTextI18n")
    private fun setUpLayoutAction() {
        fun cancelAction() {
            adapter.unSelectAll()
            b.layoutActionRecords.root.visibility = View.GONE
            setClickRecord()
        }
        val binding = b.layoutActionRecords

        binding.actionSelectAll.setOnClickListener {
            adapter.selectAll()
        }
        binding.actionDelete.setOnClickListener {
            val recordsSelected = adapter.getRecordsSeleted()
            if (recordsSelected.isEmpty()) {
               return@setOnClickListener
            }

            DialogConfirm(act)
                .setTitle(getStr(R.string.delete))
                .setMess("${getStr(R.string.delete)} ${recordsSelected.size} ${getString(R.string.record)}?")
                .setTextBtnOk(getStr(R.string.delete))
                .setTextBtnCancel(getStr(R.string.Cancel))
                .setBtnOkClick {
                    recordsSelected.forEach {
                        val model = act.model.value!!
                        model.deleteRecord(it)
                        act.model.value = model
                        ModelDB.update(model)
                        cancelAction()
                    }
                    it.cancel()
                }
                .setBtnCancelClick {
                    it.cancel()
                }.show()
        }
        binding.actionEdit.setOnClickListener {
            // only one record can be edit
            val intent = Intent(context, ViewDetailRecordActivity::class.java)
            val record = adapter.getRecordsSeleted()[0].asJsonObject
            record.remove("is_selected")
            cancelAction()

            intent.putExtra("recordString", record.toString())
            intent.putExtra("model", act.model.value)
            intent.action = ViewDetailRecordActivity.ACTION_EDIT_RECORD

            startActivityForResult(intent, 123)
        }
        binding.actionCancel.setOnClickListener {
           cancelAction()
        }
    }

    private fun hideView() {
        b.layoutSearch.visibility = View.GONE
        b.layoutActionRecords.root.visibility = View.GONE
    }
}
