package dotd.hmp.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ModelDB {
    private val db by lazy { ModelDatabase.instance }
    private val modelDao by lazy { db.modelDao() }

    fun insert(model: Model): Boolean {
        if (!checkContraintModelName(model.name))
            return false

        model.writeJsonToFile()
        modelDao.insert(model)

        return true
    }


    fun delete(vararg model: Model) {
        model.forEach {
            it.deleteFileJson()
            modelDao.delete(it)
        }
    }

    fun delete(list: List<Model>) {
        list.forEach {
            it.deleteFileJson()
            modelDao.delete(it)
        }
    }


    fun update(oldModel: Model? = null, model: Model): Boolean {
        if (oldModel != null) {
            // changed  model name
            if (!checkContraintModelName(model.name))
                return false

            if (oldModel.name.trim() != model.name.trim()) {
                model.jsonData = oldModel.getJsonDataFromFile()
                oldModel.deleteFileJson()
            }
        }


        model.writeJsonToFile()
        modelDao.update(model)
        return true
    }

    fun deleteAll() {
        modelDao.getList().forEach {
            it.deleteFileJson()
        }
        modelDao.deleteAll()
    }

    fun getList() = modelDao.getList()

    fun getLiveData() = modelDao.getLiveData()

    fun getModel(id: Int) = modelDao.getModel(id)


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
                modelDao.insert(it)
            }
            withContext(Dispatchers.Main) {
                onComplete(true)
            }
        }
    }


    fun deleteInbackground(model: Model, onComplete: () -> Unit) {
        GlobalScope.launch {
            model.deleteFileJson()
            modelDao.delete(model)
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }


    fun updateInBackground(oldModel: Model, model: Model, onComplete: (isSuccess: Boolean) -> Unit) {
        if (!checkContraintModelName(model.name)) {
            onComplete(false)
            return
        }

        GlobalScope.launch {
            if (oldModel.name.trim() != model.name.trim()) {
                model.jsonData = oldModel.getJsonDataFromFile()
                oldModel.deleteFileJson()
            }

            model.writeJsonToFile()
            modelDao.update(model)
            withContext(Dispatchers.Main) {
                onComplete(true)
            }
        }
    }


    fun deleteAllInbackground(onComplete: () -> Unit) {
        GlobalScope.launch {
            modelDao.getList().forEach {
                it.deleteFileJson()
            }
            modelDao.deleteAll()

            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun getListInBackground(onComplete: (list: List<Model>) -> Unit) {
        GlobalScope.launch {
            val list = modelDao.getList()
            withContext(Dispatchers.Main) {
                onComplete(list)
            }
        }
    }

    fun getLiveDataInBackground(onComplete: (list: LiveData<List<Model>>) -> Unit) {
        GlobalScope.launch {
            val liveData = modelDao.getLiveData()
            withContext(Dispatchers.Main) {
                onComplete(liveData)
            }
        }
    }

    fun getModelInBackground(id: Int, onComplete: (model: Model) -> Unit) {
        GlobalScope.launch {
            val model = modelDao.getModel(id)
            withContext(Dispatchers.Main) {
                onComplete(model)
            }
        }
    }

    private fun checkContraintModelName(modelName: String): Boolean {
        val list = modelDao.getList()
        for (model in list) {
            if (modelName == model.name)
                return false
        }
        return true
    }
}
