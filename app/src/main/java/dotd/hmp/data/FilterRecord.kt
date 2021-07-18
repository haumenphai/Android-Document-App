package dotd.hmp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.gson.Gson
import dotd.hmp.R
import dotd.hmp.hepler.getStr

@Entity
class FilterRecord {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var modelId: Int = 0
    var _field: String = ""      // json
    var operator: String = ""
    var value: String = ""

    @Ignore
    var isChecked = false


    override fun toString(): String {
        return "${getField().fieldName} ${getOperatorToShow()} $value"
    }

    fun getField(): Field {
        return Gson().fromJson(_field, Field::class.java)
    }

    fun setFiled(field: Field) {
        this._field = Gson().toJson(field)
    }

    fun getOperatorToShow(): String {
        return operatorAvailable.get(operator)!!
    }

    companion object {
        val operatorAvailable = mapOf(
            ">" to ">",
            "<" to ">",
            "=" to "=",
            ">=" to ">=",
            "<=" to "<=",
            "!=" to getStr(R.string.not_equal),
            "contains" to getStr(R.string.contains),
            "not contains" to getStr(R.string.not_contains)
        )
    }

}

@Dao
interface FilterRecordDao {
    @Insert
    fun insert(vararg filterRecord: FilterRecord)

    @Update
    fun update(vararg filterRecord: FilterRecord)

    @Delete
    fun delete(vararg filterRecord: FilterRecord)

    @Query("DELETE FROM filterrecord")
    fun deleteAll()

    @Query("SELECT * FROM filterrecord")
    fun getList(): List<FilterRecord>

    @Query("SELECT * FROM filterrecord")
    fun getLiveData(): LiveData<List<FilterRecord>>

    @Query("SELECT * FROM filterrecord WHERE modelId=:modelId")
    fun getFilterRecordList(modelId: Int): List<FilterRecord>

    @Query("SELECT * FROM filterrecord WHERE modelId=:modelId")
    fun getFilterRecordLiveData(modelId: Int): LiveData<List<FilterRecord>>
}


