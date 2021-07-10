package dotd.hmp.data

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
}
