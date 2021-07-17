package dotd.hmp.data

import androidx.lifecycle.LiveData

object FilterRecordDB {
    private val db by lazy { ModelDatabase.instance }
    private val modelDao by lazy { db.modelDao() }
    private val filterRecordDao by lazy { db.filterRecordDao() }

    fun getList(modelId: Int): List<FilterRecord> {
        return modelDao.getFilterRecordList(modelId)
    }

    fun getLiveData(modelId: Int): LiveData<List<FilterRecord>> {
        return modelDao.getFilterRecordLiveData(modelId)
    }

    fun insert(filterRecord: FilterRecord) {
        filterRecordDao.insert(filterRecord)
    }

    fun update(filterRecord: FilterRecord) {
        filterRecordDao.update(filterRecord)
    }

    fun delete(filterRecord: FilterRecord) {
        filterRecordDao.delete(filterRecord)
    }
}