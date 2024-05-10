package com.example.mobilumtracker.db

import android.content.Context
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
    private val eventsDao=db.eventDao()
    private val mileageDao=db.mileageDao()

    suspend fun getEvent(eventID: Long): Event? = withContext(Dispatchers.IO) {
        return@withContext eventsDao.findById(eventID)
    }

    suspend fun getEvents(): List<Event> = withContext(Dispatchers.IO) {
        return@withContext eventsDao.findAll()
    }

    suspend fun getMileage(id: Short): Int = withContext(Dispatchers.IO) {
        return@withContext mileageDao.getMileage(id)
    }

    suspend fun setMileage(id: Short, mileage: Int) = withContext(Dispatchers.IO) {
        mileageDao.setMileage(id, mileage)
    }

}