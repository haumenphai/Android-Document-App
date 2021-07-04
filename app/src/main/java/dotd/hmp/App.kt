package dotd.hmp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dotd.hmp.databinding.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.Serializable
import java.lang.Exception
import java.util.*

@SuppressLint("StaticFieldLeak")
class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        lateinit var context: Context
            private set
    }
}


const val NO_ICON = -1

@Entity
class Model: Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var name: String = ""
    var icon: Int = NO_ICON
    var jsonFields: String = ""
    var jsonData: String = "[]"
    var sequence: Int = 0
    var description = ""

    constructor()

    @Ignore
    constructor(name: String, icon: Int) {
        this.name = name
        this.icon = icon
    }

    companion object {
        val itemAddNewModel by lazy {
            Model("Add", Color.RED).apply {
                description = "add new model"
                icon = R.drawable.ic_baseline_add_24
            }
        }
    }

    fun setFieldList(list: MutableList<Field>) {
        this.jsonFields = Gson().toJson(list)
    }

    fun getFieldList(): List<Field> = Gson().fromJson(this.jsonFields, Array<Field>::class.java).asList()

    fun addNewRecord(jsonObj: org.json.JSONObject) {
        if (!jsonObj.isJsonObjRecordValidate()) {
            throw Exception("Exception when add new record, jsonOject not enough field when compared to fieldList of Model")
        }
        val jsonArray = getJsonArray()
        jsonArray.add(Gson().fromJson(jsonObj.toString(), JsonObject::class.java))
        jsonData = jsonArray.toString()
    }

    fun getJsonArray(): com.google.gson.JsonArray = Gson().fromJson(jsonData, JsonArray::class.java)

    private fun org.json.JSONObject.isJsonObjRecordValidate(): Boolean {
        getFieldList().forEach {
            if (!this.has(it.fieldName)) {
                return false
            }
        }
        return true
    }

    fun sortByField(fieldName: String): Model {
        val jsonArr = getJsonArray()
        if (!hasField(fieldName) || jsonArr.size() == 0) {
            return this
        }

        val model = this.clone()
        val jsonData = getJsonArray().sortedWith(compareBy(
            {it.asJsonObject.get(fieldName).asJsonObject.get("value").asString},
            {it.asJsonObject.get(fieldName).asJsonObject.get("value").asString}
        )).toString()
        model.jsonData = jsonData

        return model
    }

    fun hasField(fieldName: String): Boolean {
        getFieldList().forEach {
            if (it.fieldName == fieldName)
                return true
        }
        return false
    }

    fun isItemAddNewModel(): Boolean = itemAddNewModel == this

    fun hasIcon(): Boolean = icon != NO_ICON

    fun clone(): Model {
        val model = Model()
        model.id = this.id
        model.name = this.name
        model.icon = this.icon
        model.jsonFields = this.jsonFields
        model.jsonData = this.jsonData
        model.sequence = this.sequence
        model.description = this.description
        return model
    }

}


enum class FieldType {
    TEXT, NUMBER
}


class Field(var fieldName: String, var fieldType: FieldType) {
    fun isValid(): Boolean = fieldName.trim() != ""
}


@Dao
interface ModelDao {
    @Insert
    fun insert(vararg model: Model)

    @Update
    fun update(vararg model: Model)

    @Delete
    fun delete(vararg model: Model)

    @Query("DELETE FROM model")
    fun deleteAll()

    @Query("SELECT * FROM model ORDER BY sequence")
    fun getList(): List<Model>

    @Query("SELECT * FROM model ORDER BY sequence")
    fun getLiveData(): LiveData<List<Model>>

}


@Database(entities = [Model::class], version = 1)
abstract class ModelDatabase: RoomDatabase() {
    abstract fun dao(): ModelDao

    companion object {
        val instance: ModelDatabase by lazy {
            Room.databaseBuilder(MyApplication.context, ModelDatabase::class.java, "model_database")
                .allowMainThreadQueries()
                .addCallback(callback)
                .build()
        }

        private val callback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                GlobalScope.launch(Dispatchers.IO) {

                }
            }
        }


    }
}


object ModelDB {
    private val db by lazy { ModelDatabase.instance }
    private val dao by lazy { db.dao() }

    fun insert(vararg model: Model) = model.forEach { dao.insert(it) }

    fun delete(vararg model: Model) = model.forEach { dao.delete(it) }

    fun update(vararg model: Model) = model.forEach { dao.update(it) }

    fun deleteAll() = dao.deleteAll()

    fun getList() = dao.getList()

    fun getLiveData() = dao.getLiveData()
}


object ModelDemoDatas {

    fun getModelStudent(): Model {
        val fieldList = mutableListOf<Field>()
        fieldList.add(Field("name", FieldType.TEXT))
        fieldList.add(Field("age", FieldType.NUMBER))

        val model = Model().apply {
            name = "Student"
            icon = R.drawable.ic_clear
            setFieldList(fieldList)
            jsonData = """
                [
                    {
                        "name": {
                            "fieldType": "${FieldType.TEXT}",
                            "value": "Aeter"
                        },
                        "age": {
                            "fieldType": "${FieldType.NUMBER}", 
                            "value": 20
                        }
                    },
                    {
                        "name": {
                            "fieldType": "${FieldType.TEXT}",
                            "value": "Ceter"
                        },
                        "age": {
                            "fieldType": "${FieldType.NUMBER}", 
                            "value": 40
                        }
                    },
                    {
                        "name": {
                            "fieldType": "${FieldType.TEXT}",
                            "value": "Bocke"
                        },
                        "age": {
                            "fieldType": "${FieldType.NUMBER}",  
                            "value": 19
                        }
                    }
                    
                ]
            """.trimIndent()
        }
        return model
    }
}


class CreateModelAcivity : AppCompatActivity() {
    private val b by lazy { ActivityCreateModelBinding.inflate(layoutInflater) }
    private val fieldList = mutableListOf<Field>()
    private val modelName by lazy { intent.getStringExtra("model_name")!! }
    private val modelIcon by lazy { intent.getIntExtra("icon", -1) }
    private val fieldCount = MutableLiveData<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)
        b.layoutOneFieldDefault.root.visibility = View.GONE
        fieldList.clear()

        addNewLayoutOneFieldToRootLayout()
        setClick()
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun setClick() {
        b.btnAddNewField.setOnClickListener {
            addNewLayoutOneFieldToRootLayout()
        }

        b.btnCreate.setOnClickListener {
            fieldList.forEach {
                if (!it.isValid()) {
                    showMess("Field Name mustn't be empty")
                    return@setOnClickListener
                }
            }
            DialogConfirmCreateModel(this).apply {
                b.tvContent.setText(getTextFieldsConfirm())
                b.tvModelName.setText(modelName)

                b.btnBack.setOnClickListener { dialog.cancel() }
                b.btnCreate.setOnClickListener {
                    val model = Model(modelName, modelIcon)
                    model.setFieldList(fieldList)
                    ModelDB.insert(model)
                    finish()
                }
                dialog.show()
            }
        }
        b.scrollView.setOnTouchListener { v, event ->
            UIHelper.hideKeyboardFrom(this, b.root)
            false
        }

        fieldCount.observeForever {
            supportActionBar!!.title = "Create $modelName... ($it field)"
        }
    }



    private fun addNewLayoutOneFieldToRootLayout() {
        if (fieldCount.value != null) {
            fieldCount.postValue(fieldCount.value!! + 1)
        } else {
            fieldCount.postValue(1)
        }

        val layoutOneField = LayoutInflater.from(this).inflate(R.layout.layout_one_field, null)
        val b2 = LayoutOneFieldBinding.bind(layoutOneField)
        b.layoutContentScroll.addView(layoutOneField)

        val field = Field("", FieldType.TEXT)
        fieldList.add(field)

        b2.btnDeleteField.setOnClickListener {
            b.layoutContentScroll.removeView(layoutOneField)
            fieldList.remove(field)
            fieldCount.postValue(fieldCount.value!! - 1)
        }

        val listSpinner = listOf("Text", "Number")
        b2.spinner1.adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, listSpinner)

        b2.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (listSpinner[position]) {
                    "Text" -> field.fieldType = FieldType.TEXT
                    "Number" -> field.fieldType = FieldType.NUMBER
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        b2.editFieldName.addTextChangedListener {
            field.fieldName = it.toString()
        }

    }

    private fun showMess(mess: String) = AlertDialog.Builder(this).setMessage(mess).show()

    private fun getTextFieldsConfirm(): String {
        var textContent = ""
        fieldList.forEach {
            textContent += "${it.fieldName}: ${it.fieldType.toString().title()}\n"
        }
        return textContent
    }

    //   b2.editFieldName.inputType = InputType.TYPE_CLASS_TEXT +
    //                                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS

    //                         b2.editFieldName.inputType = InputType.TYPE_CLASS_NUMBER
}


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

class ViewDataModelActivity : AppCompatActivity() {
    private val b by lazy { ActivityViewDataModelBinding.inflate(layoutInflater) }
    val model: MutableLiveData<Model> by lazy { MutableLiveData<Model>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)
        model.value = intent.getSerializableExtra("model") as Model

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ViewDataModelFragment())
            .commit()

    }

    fun addFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().add(R.id.container, fragment).commit()


    fun removeFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().remove(fragment).commit()


}


class DataModelAdpater():
    RecyclerView.Adapter<DataModelAdpater.DataModelHolder>() {

    private lateinit var jsonArr: JsonArray
    private lateinit var model: Model

    fun setModel(model: Model) {
        this.model = model
        this.jsonArr = model.getJsonArray()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataModelHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_data_model, parent, false)
        return DataModelHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DataModelHolder, position: Int) {
        val b = holder.b
        val jsonObject = jsonArr[position].asJsonObject
        b.tvSequence.text = "#${position + 1}"

        var text = ""
        jsonObject.keySet().forEach { fieldName ->
            val fieldType = jsonObject.get(fieldName).asJsonObject.get("fieldType").asString
            val value = jsonObject.get(fieldName).asJsonObject.get("value").asString
            if (fieldType == FieldType.TEXT.toString() ||
                fieldType == FieldType.NUMBER.toString()
            ) {
                text += """
                    <font color="black">
                           <b>${fieldName}:</b>
                    </font> $value <br/>
                """.trimIndent()
            }
        }
        b.tvData.setTextHTML(text)
    }

    override fun getItemCount(): Int = jsonArr.size()

    lateinit var onClickItem: (jsonObj: JsonObject) -> Unit

    inner class DataModelHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val b = ItemDataModelBinding.bind(itemView)

        init {
            b.background.setOnClickListener {
                onClickItem(jsonArr[layoutPosition].asJsonObject)
            }
        }
    }

}


class ModelApdater(private var list: List<Model> = mutableListOf()): RecyclerView.Adapter<ModelApdater.ModelHolder>() {

    @JvmName("setList1")
    fun setList(list: List<Model>) {
        this.list = list
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_model, parent, false)
        return ModelHolder(view)
    }

    override fun onBindViewHolder(holder: ModelHolder, position: Int) {
        val b = holder.b
        val model = list[position]

        if (model.hasIcon())
            b.icon.setImageResource(model.icon)
        else
            b.icon.setImageResource(R.drawable.ic_default_model_icon)

        b.modelName.text = model.name
    }

    override fun getItemCount(): Int = list.size


    lateinit var onClickItem: (model: Model) -> Unit

    inner class ModelHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val b: ItemModelBinding = ItemModelBinding.bind(itemView)
        init {
            b.background.setOnClickListener {
                onClickItem(list[layoutPosition])
            }
        }
    }
}


class DialogAddNewModel(val context: Context) {
    private val view by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_add_new_model, null) }
    val b by lazy { DialogAddNewModelBinding.bind(view) }
    val dialog by lazy { Dialog(context) }

    init {
        dialog.setContentView(b.root)
        dialog.setCanceledOnTouchOutside(false)
    }

    fun setBtnOkClick(callBack: (modelName: String, icon: Int) -> Unit) {
        var icon = NO_ICON

        b.btnOk.setOnClickListener {
            val text = b.edittext.text.toString()
            if (text.trim().isEmpty()) {
                android.app.AlertDialog.Builder(context).setMessage("Model name mustn't be empty!")
                return@setOnClickListener
            }
            callBack(text, icon)
            cancel()
        }
        b.imgIcon.setOnClickListener {
            UIHelper.hideKeyboardFrom(context, b.edittext)
            DialogPickIcon(context).apply {
                setItemIconClick { itemIcon ->
                    icon = itemIcon.iconResource
                    cancel()
                    this@DialogAddNewModel.b.imgIcon.setImageResource(icon)
                }
                show()
            }
        }

    }

    fun setBtnCancelClick(callBack: () -> Unit) {
        b.btnCancel.setOnClickListener {
            callBack()
            cancel()
        }
    }

    fun cancel() = dialog.cancel()
    fun show() = dialog.show()
}


class DialogConfirmCreateModel(val context: Context) {
    private val view by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_confirm_create_model, null) }
    val b by lazy { DialogConfirmCreateModelBinding.bind(view) }
    val dialog by lazy { Dialog(context) }

    init {
        dialog.setContentView(view)
    }
}


class DialogEditTextLarge(val context: Context, val text: String) {

    private val view by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_show_edittext_large, null) }
    val b by lazy { DialogShowEdittextLargeBinding.bind(view) }
    val dialog by lazy { Dialog(context) }

    init {
        dialog.setContentView(b.root)
        b.edittext.setText(text)
    }

    fun setOnCancelListener(callBack: (text: String) -> Unit) {
        dialog.setOnCancelListener {
            callBack.invoke(b.edittext.text.toString())
        }
    }

    fun show() = dialog.show()
    fun cancel() = dialog.cancel()
}

class DialogPickIcon(val context: Context) {
    private val view by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_pick_icon, null) }
    val b by lazy { DialogPickIconBinding.bind(view) }
    val dialog by lazy { Dialog(context) }

    private val adapter = ItemIconApdapter()

    init {
        dialog.setContentView(view)
        b.recyclerView.layoutManager = GridLayoutManager(context, 4)
        b.recyclerView.adapter = adapter

        val listIcon = listOf(
            // todo: this is demo data, remove it
            ItemIcon(R.drawable.ic_baseline_add_24, ""),
            ItemIcon(R.drawable.ic_baseline_delete_24, ""),
            ItemIcon(R.drawable.ic_clear, ""),
        )
        adapter.setList(listIcon)
    }

    fun show() = dialog.show()

    fun cancel() = dialog.cancel()

    fun setItemIconClick(callBack: (itemIcon: ItemIcon) -> Unit): DialogPickIcon {
        adapter.onItemClick = {
            callBack(it)
        }
        return this
    }
}

class ItemIcon(var iconResource: Int, var name: String = "")

class ItemIconApdapter(private var list: List<ItemIcon> = mutableListOf()):
    RecyclerView.Adapter<ItemIconApdapter.ItemIconHolder>() {

    @JvmName("setList1")
    fun setList(list: List<ItemIcon>) {
        this.list = list
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemIconHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_icon, parent, false)
        return ItemIconHolder(view)
    }

    override fun onBindViewHolder(holder: ItemIconHolder, position: Int) {
        val b = holder.b
        val itemIcon = list[position]

        b.imgIcon.setImageResource(itemIcon.iconResource)
    }

    override fun getItemCount(): Int = list.size

    lateinit var onItemClick: (itemIcon: ItemIcon) -> Unit
    inner class ItemIconHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val b = ItemIconBinding.bind(itemView)
        init {
            b.imgIcon.setOnClickListener {
                onItemClick(list[layoutPosition])
            }
        }
    }
}


class AddModelRecordFragment: Fragment() {
    private lateinit var b: FragmentAddModelRecordBinding
    private val act: ViewDataModelActivity by lazy { activity as ViewDataModelActivity }
    private val jsonObj = org.json.JSONObject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        b = FragmentAddModelRecordBinding.inflate(inflater, container, false)
        setUpLayout()
        setClick()
        return b.root
    }

    @SuppressLint("SetTextI18n")
    fun setUpLayout() {
        val model = act.model.value!!
        model.getFieldList().forEach { field ->
            val jsonObject2 = JSONObject()

            when (field.fieldType) {
                FieldType.NUMBER -> {
                    val view  = LayoutInflater.from(act).inflate(R.layout.field_number, null)
                    val binding = FieldNumberBinding.bind(view)
                    binding.tvFieldName.text = "${field.fieldName}:"
                    binding.editContent.addTextChangedListener { text ->
                        jsonObject2.put("fieldType", field.fieldType)
                        jsonObject2.put("value", text.toString())
                        jsonObj.put(field.fieldName, jsonObject2)
                    }
                    b.layoutField.addView(view)
                }
                FieldType.TEXT -> {
                    val view  = LayoutInflater.from(act).inflate(R.layout.field_text, null)
                    val binding = FieldNumberBinding.bind(view)
                    binding.tvFieldName.text = "${field.fieldName}:"
                    binding.editContent.addTextChangedListener { text ->
                        jsonObject2.put("fieldType", field.fieldType)
                        jsonObject2.put("value", text.toString())
                        jsonObj.put(field.fieldName, jsonObject2)
                    }
                    b.layoutField.addView(view)
                }
            }
        }
    }

    fun setClick() {
        b.btnCreateRecord.setOnClickListener {
            val model = act.model.value!!
            model.addNewRecord(jsonObj)
            ModelDB.update(model)
            act.model.value = model

            act.removeFragment(this)
            UIHelper.hideKeyboardFrom(act, b.root)
        }
    }
}

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

fun TextView.setTextHTML(html: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE)
    } else {
        this.setText(Html.fromHtml(html), TextView.BufferType.SPANNABLE)
    }
}


fun String.title(): String {
    if (this.isEmpty()) return this
    var result = this.substring(0,1).toUpperCase(Locale.ROOT)
    result += this.substring(1).toLowerCase(Locale.ROOT)
    return result
}

object UIHelper {
    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(context: Context) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
}
