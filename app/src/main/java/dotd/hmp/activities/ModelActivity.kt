package dotd.hmp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import dotd.hmp.R
import dotd.hmp.adapter.ModelApdater
import dotd.hmp.data.*
import dotd.hmp.databinding.ActivityModelBinding
import dotd.hmp.dialog.DialogAddNewModel
import dotd.hmp.dialog.DialogConfirm
import dotd.hmp.dialog.DialogEditModel
import dotd.hmp.dialog.DialogShowMess
import dotd.hmp.hepler.UIHelper
import kotlinx.coroutines.*


class ModelActivity : AppCompatActivity() {
    private val b: ActivityModelBinding by lazy { ActivityModelBinding.inflate(layoutInflater) }
    private val adapter: ModelApdater = ModelApdater()

    val list = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)
        setConfigToolBar()
        setUpRecyclerView()

        b.btnTest.setOnClickListener {
            // TODO: remove test
            val db = ModelDatabase.instance.modelDao()
            db.deleteAll()
        }

        b.btnInsert.setOnClickListener {
            // TODO: remove test
            val student = ModelDemoDatas.getModelStudentTest()
            ModelDB.insert(student)
            Log.d("AAA", student.getRecordList().toString())
            GlobalScope.launch {
                val model = ModelDemoDatas.getModelStudentTest(1000)
                ModelDB.insert(model)
            }
        }
    }

    private fun setConfigToolBar() {
        setSupportActionBar(b.toolBar)
    }

    private fun setUpRecyclerView() {
        b.recyclerView.adapter = adapter
        b.recyclerView.layoutManager = GridLayoutManager(this, 4)

        ModelDatabase.instance.modelDao().getLiveData().observe(this, {
            val list = it.toMutableList()
            if (!list.contains(Model.itemAddNewModel))
                list.add(Model.itemAddNewModel)
            adapter.setList(list)

        })
        setClickModel()
        setClickActionModel()
    }

    private fun setClickModel() {

        fun removeItemAdd() {
            val list = adapter.getList().toMutableList()
            list.remove(Model.itemAddNewModel)
            adapter.setList(list)
        }

        adapter.onClickItem = {
            val activity = this
            if (it.isItemAddNewModel()) {
                DialogAddNewModel(this).apply {
                    setBtnOkClick { modelName, icon ->
                        UIHelper.hideKeyboardFrom(activity, b.root)
                        val intent = Intent(activity, CreateModelAcivity::class.java)
                        intent.putExtra("model_name", modelName)
                        intent.putExtra("icon", icon)
                        startActivity(intent)
                    }
                    setBtnCancelClick {
                        UIHelper.hideKeyboardFrom(activity, b.root)
                    }
                    UIHelper.showKeyboard(activity)
                    show()
                }
            } else {
                val intent = Intent(this, ViewRecordsActivity::class.java)
                intent.putExtra("model", it)
                startActivity(intent)
            }
        }
        adapter.onLongClickItem = {
            if (!it.isItemAddNewModel()) {
                b.layoutActionModels.root.visibility = View.VISIBLE
                it.isSelected = !it.isSelected
                hideActionEdit()
                removeItemAdd()

                adapter.onClickItem = {
                    if (!it.isItemAddNewModel()) {
                        it.isSelected = !it.isSelected
                        adapter.notifyDataSetChanged()
                        hideActionEdit()
                    }
                }
            }
        }
    }

    private fun setClickActionModel() {
        val b1 = b.layoutActionModels
        fun cancelAction() {
            b.layoutActionModels.root.visibility = View.GONE
            adapter.unSelectAll()
            setClickModel()

            val list = adapter.getList().toMutableList()
            if (!list.contains(Model.itemAddNewModel))
                list.add(Model.itemAddNewModel)
            adapter.setList(list)
        }

        b1.actionSelectAll.setOnClickListener {
            adapter.selectAll()
            hideActionEdit()
        }
        b1.actionDelete.setOnClickListener {
            if (adapter.getItemSelected().isEmpty()) {
                return@setOnClickListener
            }
            val models = adapter.getItemSelected()
            DialogConfirm(this)
                .setTitle(getString(R.string.delete_model))
                .setMess("${getString(R.string.delete)} ${models.size} ${getString(R.string.model)}?")
                .setTextBtnOk(getString(R.string.delete))
                .setTextBtnCancel(getString(R.string.cancel))
                .setBtnOkClick {
                    ModelDB.delete(models)
                    cancelAction()
                }.setBtnCancelClick {

                }.show()
        }
        b1.actionEdit.setOnClickListener {
            // only one model can edit
            val oldModel = adapter.getItemSelected()[0]
            DialogEditModel(this, oldModel.clone())
                .setBtnSaveClick { newModel->
                    ModelDB.update(oldModel, newModel).also { isSuccess ->
                        if (!isSuccess) DialogShowMess.showMessUpdateModelFailure(this)
                    }
                }.show()
            cancelAction()
        }
        b1.actionCancel.setOnClickListener {
            cancelAction()
        }
    }

    private fun hideActionEdit() {
        if (adapter.getItemSelected().size != 1) {
            b.layoutActionModels.actionEdit.visibility = View.INVISIBLE
        } else {
            b.layoutActionModels.actionEdit.visibility = View.VISIBLE
        }
    }

}
