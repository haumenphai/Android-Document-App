package dotd.hmp.data

import androidx.lifecycle.LiveData

object FilterRecordDB {
    private val db by lazy { ModelDatabase.instance }
    private val dao by lazy { db.filterRecordDao() }

    fun getList(modelId: Int): List<FilterRecord> {
        return dao.getFilterRecordList(modelId)
    }

    fun getLiveData(modelId: Int): LiveData<List<FilterRecord>> {
        return dao.getFilterRecordLiveData(modelId)
    }

    fun insert(filterRecord: FilterRecord) {
        dao.insert(filterRecord)
    }

    fun update(filterRecord: FilterRecord) {
        dao.update(filterRecord)
    }

    fun delete(filterRecord: FilterRecord) {
        dao.delete(filterRecord)
    }
}