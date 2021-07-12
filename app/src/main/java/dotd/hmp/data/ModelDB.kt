package dotd.hmp.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ModelDB {
    private val db by lazy { ModelDatabase.instance }
    private val dao by lazy { db.dao() }

    fun insert(vararg model: Model) = model.forEach { dao.insert(it) }
    fun insert(list: List<Model>)   = list.forEach { dao.insert(it) }

    fun delete(vararg model: Model) = model.forEach { dao.delete(it) }
    fun delete(list: List<Model>)   = list.forEach { dao.delete(it) }

    fun update(vararg model: Model) = model.forEach { dao.update(it) }
    fun update(list: List<Model>)   = list.forEach { dao.update(it) }

    fun deleteAll() = dao.deleteAll()

    fun getList() = dao.getList()

    fun getLiveData() = dao.getLiveData()

    fun getModel(id: Int) = dao.getModel(id)


    fun insertInbackgroud(vararg model: Model, onComplete: () -> Unit) {
        GlobalScope.launch {
            model.forEach { dao.insert(it) }
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun insertInbackgroud(list: List<Model>, onComplete: () -> Unit) {
        GlobalScope.launch {
            list.forEach { dao.insert(it) }
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun deleteInbackground(vararg model: Model, onComplete: () -> Unit) {
        GlobalScope.launch {
            model.forEach { dao.delete(it) }
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun deleteInbackground(list: List<Model>, onComplete: () -> Unit) {
        GlobalScope.launch {
            list.forEach { dao.delete(it) }
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun updateInBackground(vararg model: Model, onComplete: () -> Unit) {
        GlobalScope.launch {
            model.forEach { dao.update(it) }
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun updateInBackground(list: List<Model>, onComplete: () -> Unit) {
        GlobalScope.launch {
            list.forEach { dao.update(it) }
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun deleteAllInbackground(onComplete: () -> Unit) {
        GlobalScope.launch {
            dao.deleteAll()
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun getListInBackground(onComplete: (list: List<Model>) -> Unit) {
        GlobalScope.launch {
            val list = dao.getList()
            withContext(Dispatchers.Main) {
                onComplete(list)
            }
        }
    }

    fun getLiveDataInBackground(onComplete: (list: LiveData<List<Model>>) -> Unit) {
        GlobalScope.launch {
            val liveData = dao.getLiveData()
            withContext(Dispatchers.Main) {
                onComplete(liveData)
            }
        }
    }

    fun getModelInBackground(id: Int, onComplete: (model: Model) -> Unit) {
        GlobalScope.launch {
            val model = dao.getModel(id)
            withContext(Dispatchers.Main) {
                onComplete(model)
            }
        }
    }
}
