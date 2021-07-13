package dotd.hmp.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ModelDB {
    private val db by lazy { ModelDatabase.instance }
    private val dao by lazy { db.dao() }

    fun insert(vararg model: Model): Boolean {
        model.forEach {
            if (!checkContraintModelName(it.name))
                return false
        }
        model.forEach {
            it.writeJsonToFile()
            dao.insert(it)
        }
        return true
    }
    fun insert(list: List<Model>): Boolean {
        list.forEach {
            if (!checkContraintModelName(it.name))
                return false
        }
        list.forEach {
            it.writeJsonToFile()
            dao.insert(it)
        }
        return true
    }

    fun delete(vararg model: Model) {
        model.forEach {
            it.deleteFileJson()
            dao.delete(it)
        }
    }
    fun delete(list: List<Model>) {
        list.forEach {
            it.deleteFileJson()
            dao.delete(it)
        }
    }

    fun update(vararg model: Model): Boolean {
        model.forEach {
            if (!checkContraintModelName(it.name))
                return false
        }
        model.forEach {
            it.writeJsonToFile()
            dao.update(it)
        }
        return true
    }
    fun update(list: List<Model>): Boolean {
        list.forEach {
            if (!checkContraintModelName(it.name))
                return false
        }
        list.forEach {
            it.writeJsonToFile()
            dao.update(it)
        }
        return true
    }

    fun deleteAll() {
        dao.getList().forEach {
            it.deleteFileJson()
        }
        dao.deleteAll()
    }

    fun getList() = dao.getList()

    fun getLiveData() = dao.getLiveData()

    fun getModel(id: Int) = dao.getModel(id)


    fun insertInbackgroud(vararg model: Model, onComplete: (isSuccess: Boolean) -> Unit) {
        model.forEach {
            if (!checkContraintModelName(it.name)) {
                onComplete(false)
                return
            }
        }
        GlobalScope.launch {
            model.forEach {
                it.writeJsonToFile()
                dao.insert(it)
            }
            withContext(Dispatchers.Main) {
                onComplete(true)
            }
        }
    }

    fun insertInbackgroud(list: List<Model>, onComplete: (isSuccess: Boolean) -> Unit) {
        list.forEach {
            if (!checkContraintModelName(it.name)) {
                onComplete(false)
                return
            }
        }
        GlobalScope.launch {
            list.forEach {
                it.writeJsonToFile()
                dao.insert(it)
            }
            withContext(Dispatchers.Main) {
                onComplete(true)
            }
        }
    }

    fun deleteInbackground(vararg model: Model, onComplete: () -> Unit) {
        GlobalScope.launch {
            model.forEach {
                it.deleteFileJson()
                dao.delete(it)
            }
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun deleteInbackground(list: List<Model>, onComplete: () -> Unit) {
        GlobalScope.launch {
            list.forEach {
                it.deleteFileJson()
                dao.delete(it)
            }
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun updateInBackground(vararg model: Model, onComplete: (isSuccess: Boolean) -> Unit) {
        model.forEach {
            if (!checkContraintModelName(it.name)) {
                onComplete(false)
                return
            }
        }
        GlobalScope.launch {
            model.forEach {
                it.writeJsonToFile()
                dao.update(it)
            }
            withContext(Dispatchers.Main) {
                onComplete(true)
            }
        }
    }

    fun updateInBackground(list: List<Model>, onComplete: (isSuccess: Boolean) -> Unit) {
        list.forEach {
            if (!checkContraintModelName(it.name)) {
                onComplete(false)
                return
            }
        }
        GlobalScope.launch {
            list.forEach {
                it.writeJsonToFile()
                dao.update(it)
            }
            withContext(Dispatchers.Main) {
                onComplete(true)
            }
        }
    }

    fun deleteAllInbackground(onComplete: () -> Unit) {
        GlobalScope.launch {
            dao.getList().forEach {
                it.deleteFileJson()
            }
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

    private fun checkContraintModelName(modelName: String): Boolean {
        val list = dao.getList()
        for (model in list) {
            if (modelName == model.name)
                return false
        }
        return true
    }
}
