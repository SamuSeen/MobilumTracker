package com.example.mobilumtracker.db

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.room.migration.Migration
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
    version = 46,
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
                db.query("select 1", emptyArray())
            }
        }
        //nie działa populowanie dunno why
        private suspend fun populateDatabase(
            eventDao: EventDao,
            mileageDao: MileageDao) {
            withContext(Dispatchers.IO) {
                Log.i("DB","Populating empty database.")
                eventDao.insertAll(
                    Event(
                    "Przegląd",
                    365,
                        "2024-05-05",
                    20000,
                        19800,
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus pretium."),
                    Event(
                        "Olej",
                        365,
                            "2024-08-15",
                        15000,
                            19800,
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus pretium.")
                )
                mileageDao.insertAll(
                    Mileage(
                        20000,
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
            Log.i("DB","Database initialization")
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    dbname
                )
                    .addMigrations(MIGRATION_43_44)
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }


}

val MIGRATION_43_44 = object : Migration(43, 44) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `Event_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `Event` TEXT NOT NULL, `Days` INTEGER NOT NULL, `LastDate` TEXT NOT NULL, `Distance` INTEGER NOT NULL, `LastDistance` INTEGER NOT NULL, `Description` TEXT NOT NULL)")
        db.execSQL("INSERT INTO `Event_new` (`id`, `Event`, `Days`, `LastDate`, `Distance`, `LastDistance`, `Description`) SELECT `id`, `Event`, `Days`, `LastTime`, `Distance`, `LastDistance`, `Description` FROM `Event`")
        db.execSQL("DROP TABLE IF EXISTS `Event`")
        db.execSQL("ALTER TABLE `Event_new` RENAME TO `Event`")
    }
}

/*use for returned elements
data class NameTuple(
    @ColumnInfo(name = "first_name") val firstName: String?,
    @ColumnInfo(name = "last_name") val lastName: String?
)*/