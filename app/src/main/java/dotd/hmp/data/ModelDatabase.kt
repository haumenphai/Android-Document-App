package dotd.hmp.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dotd.hmp.Model
import dotd.hmp.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
                    instance.dao().insert()
                }
            }
        }


    }
}