@file:Suppress("unused", "unused")

package com.example.mobilumtracker.db

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.mobilumtracker.Config
import com.example.mobilumtracker.SSUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume

/**
 * Main object holding session information
 */
object Running {
    //to jest uruchamiane na application context nie activity context
    @SuppressLint("StaticFieldLeak")
    lateinit var dataProcessor: DataProcessor
    private var scopeDatabase: CoroutineScope? = null
    private var mileage = 0
    private var vehicleId = 0
    private var sortMode = 0

    val isInitialized: Boolean
        get() = Running::dataProcessor.isInitialized

    suspend fun init(context: Context, coroutineScope: CoroutineScope, id: Int = 1) {
        scopeDatabase = coroutineScope
        dataProcessor = DataProcessor(context, coroutineScope, Config.DB_NAME.value.toString())
        setVehicle(id)
        if (dataProcessor.getEvents().isEmpty()) dataProcessor.init()
        mileage= dataProcessor.getMileage(vehicleId)
        Log.i("DB","Loaded database with ${dataProcessor.getEvents().size} events and $mileage mileage for vehicle $vehicleId")
        Log.i("DB","Database initialized")
        SSUtils.initializeNotifications(context, coroutineScope)
    }

    fun setVehicle(id:Int ){
        this.vehicleId = id
        scopeDatabase?.launch {
            mileage = dataProcessor.getMileage(id)
        }
    }

    fun getMileage(): Int {
        return mileage
    }

    fun setMileage(mileage: Int) {
        this.mileage = mileage
        scopeDatabase?.launch {
            dataProcessor.setMileage(vehicleId, mileage)
        }
        Log.i("DB", "Mileage updated to $mileage for vehicle $vehicleId")
    }
    fun addMileage(mileage: Int) {
        val dist = this.mileage + mileage
        this.mileage = dist
        scopeDatabase?.launch {
            dataProcessor.setMileage(vehicleId, dist)
        }
    }

    fun getVehicleId(): Int {
        return vehicleId
    }

    suspend fun getEvent(id: Long): Event? = suspendCancellableCoroutine { continuation ->
        scopeDatabase?.launch {
            val event: Event? = dataProcessor.getEvent(id)
            continuation.resume(event)
        }
    }

    fun setSortMode(mode: Int) {
        if (mode < 0 || mode > 3) return
        sortMode = mode
    }

    fun getSortMode(): Int {
        return sortMode
    }

    /**
     * @param sort 0 - id, 1 - time remaining, 2 - distance remaining, 3 - alphabetically
     */
    suspend fun getEvents(sort: Int = sortMode): List<Event> = suspendCancellableCoroutine { continuation ->
        scopeDatabase?.launch {
            val events: List<Event> = dataProcessor.getEvents()
            val sortedEvents = when (sort) {
                1 -> sortByTimeRemaining(events)
                2 -> sortByDistanceRemaining(events)
                3 -> sortAlphabetically(events)
                else -> events
            }
            continuation.resume(sortedEvents)
        }
    }

    /**
     * @param date date in format YYYY-MM-DD
     */
    suspend fun getEvents(date: String, sort: Int = 0): List<Event> {
        val events: List<Event> = getEvents(sort)
        return events.filter { SSUtils.getTargetDate(it).format(DateTimeFormatter.ISO_LOCAL_DATE) == date }
    }

    /**
     * @param lastDate last date in format YYYY-MM-DD
     * @param days frequency in days
     * @param distance frequency in distance units
     */
    fun addEvent(name: String, description: String, days: Int, distance: Int,
                 lastDate: String = LocalDate.now().toString(),lastMileage: Int = mileage) {
        scopeDatabase?.launch {
            dataProcessor.addEvent(Event(
                name,
                days,
                lastDate,
                distance,
                lastMileage,
                description
            ))
        }
    }

    fun deleteEvent(id: Long) {
        scopeDatabase?.launch {
            val event = dataProcessor.getEvent(id)
            if (event != null)
                dataProcessor.deleteEvent(event)
        }
    }

    private fun sortByTimeRemaining(events: List<Event>): List<Event> {
        val currentDate = LocalDate.now()
        return events.sortedBy { event ->
            val nextDate = LocalDate.parse(event.lastDate).plusDays(event.days.toLong())
            Duration.between(currentDate.atStartOfDay(), nextDate.atStartOfDay()).toDays()
        }
    }
    private fun sortByDistanceRemaining(events: List<Event>, currentMileage: Int = mileage): List<Event> {
        return events.sortedBy { event ->
            event.lastDistance + event.distance - currentMileage
        }
    }
    private fun sortAlphabetically(events: List<Event>): List<Event> {
        return events.sortedBy { event -> event.event }
    }

    fun updateEvent(event: Event) {
        scopeDatabase?.launch {
            dataProcessor.updateEvent(event)
        }
    }


}
