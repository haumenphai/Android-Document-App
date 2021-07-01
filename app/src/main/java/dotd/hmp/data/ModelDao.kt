package dotd.hmp.data

import androidx.lifecycle.LiveData
import androidx.room.*

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