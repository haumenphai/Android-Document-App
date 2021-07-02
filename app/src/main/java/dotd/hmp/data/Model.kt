package dotd.hmp.data

import android.graphics.Color
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.Gson

@Entity
class Model {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var name: String = ""
    var color: Int = 0
    var jsonFields: String = ""
    var jsonData: String = ""
    var sequence: Int = 0
    var description = ""

    constructor()

    @Ignore
    constructor(name: String, color: Int) {
        this.name = name
        this.color = color
    }

    @Ignore
    constructor(name: String, color: Int, jsonFields: String) {
        this.name = name
        this.color = color
        this.jsonFields = jsonFields
    }

    companion object {
        val itemAddNewModel by lazy {
                Model("Add", Color.RED).apply {
                description = "add new model"
            }
        }
    }

    fun setFieldList(list: MutableList<Field>) {
        this.jsonFields = Gson().toJson(list)
    }

    fun getFieldList(): List<Field> = Gson().fromJson(this.jsonFields, Array<Field>::class.java).asList()

}

enum class FieldType {
    TEXT, NUMBER
}

class Field(var fieldName: String, var fieldType: FieldType) {
    fun isValid(): Boolean = fieldName.trim() != ""
}
