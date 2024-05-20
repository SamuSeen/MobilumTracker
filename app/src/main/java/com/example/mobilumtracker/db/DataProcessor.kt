package com.example.mobilumtracker.db

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/** A class for executing operations on database
 *
 */
class DataProcessor(context: Context,
                    databaseScope: CoroutineScope,
                    dbname: String)  {
    private var db: AppDatabase = AppDatabase.getDatabase(context, databaseScope, dbname)
    private val eventDao=db.eventDao()
    private val mileageDao=db.mileageDao()

    suspend fun init() = withContext(Dispatchers.IO) {
        Log.i("DB","Database initialization")
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

    suspend fun getEvent(eventID: Long): Event? = withContext(Dispatchers.IO) {
        return@withContext eventDao.findById(eventID)
    }
    suspend fun getEvents(): List<Event> = withContext(Dispatchers.IO) {
        return@withContext eventDao.findAll()
    }
    suspend fun addEvent(event: Event) = withContext(Dispatchers.IO) {
        eventDao.insertAll(event)
        Log.i("DB","Adding event to database")
    }
    suspend fun deleteEvent(event: Event) = withContext(Dispatchers.IO) {
        eventDao.delete(event)
    }

    suspend fun getMileage(id: Int): Int = withContext(Dispatchers.IO) {
        return@withContext mileageDao.getMileage(id)
    }

    suspend fun setMileage(id: Int, mileage: Int) = withContext(Dispatchers.IO) {
        mileageDao.setMileage(id, mileage)
    }


}