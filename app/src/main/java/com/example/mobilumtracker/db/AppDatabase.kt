package com.example.mobilumtracker.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Tablice to clasy z @Entity
 */
@Database(entities = [
    Event::class,
    Mileage::class],
    version = 12,
    exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun mileageDao(): MileageDao

    //Inicjalizacja ze wstępnymi wartościami
    private class DatabaseCallback(private val scope: CoroutineScope) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(
                        database.eventDao(),
                        database.mileageDao()
                    )
                }
            }
        }
        //nie działa populowanie dunno why
        private suspend fun populateDatabase(
            eventDao: EventDao,
            mileageDao: MileageDao
        ) {
            withContext(Dispatchers.IO) {
                eventDao.insertAll(
                    Event(
                    "Przegląd",
                    365,
                        "2024-05-05",
                    20000,
                        19800,
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus pretium.")
                )
                mileageDao.insertAll(
                    Mileage(
                        0,
                        0
                    )
                )
            }
        }

    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context,scope: CoroutineScope,dbname:String): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    dbname
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }


}

/*use for returned elements
data class NameTuple(
    @ColumnInfo(name = "first_name") val firstName: String?,
    @ColumnInfo(name = "last_name") val lastName: String?
)*/