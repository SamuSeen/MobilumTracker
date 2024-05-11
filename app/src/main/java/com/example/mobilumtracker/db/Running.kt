@file:Suppress("unused", "unused")

package com.example.mobilumtracker.db

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.room.ColumnInfo
import com.example.mobilumtracker.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDate
import kotlin.coroutines.resume
import kotlin.properties.Delegates
import java.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
object Running {
    //to jest uruchamiane na application context nie activity context
    @SuppressLint("StaticFieldLeak")
    lateinit var dataProcessor: DataProcessor
    private var scopeDatabase: CoroutineScope? = null
    private var mileage by Delegates.notNull<Int>()
    private var vehicleId by Delegates.notNull<Short>()
    private var events: List<Event>? = null

    val isInitialized: Boolean
        get() = Running::dataProcessor.isInitialized

    fun init(context: Context, coroutineScope: CoroutineScope, id: Short = 0) {
        scopeDatabase = coroutineScope
        dataProcessor = DataProcessor(context, coroutineScope, Config.DB_NAME.value.toString())
        setVehicleId(id)
        coroutineScope.launch {
            mileage= dataProcessor.getMileage(vehicleId)
            events = dataProcessor.getEvents()
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
    }
    fun addMileage(mileage: Int) {
        this.mileage += mileage
    }

    fun setVehicleId(vehicleId: Short) {
        this.vehicleId = vehicleId
    }

    fun getVehicleId(): Short {
        return vehicleId
    }

    suspend fun getEvent(id: Long): Event? = suspendCancellableCoroutine { continuation ->
        scopeDatabase?.launch {
            val event: Event? = dataProcessor.getEvent(id)
            continuation.resume(event)
        }
    }

    /**
     * @param sort 0 - id, 1 - time remaining, 2 - distance remaining, 3 - alphabetically
     */
    suspend fun getEvents(sort: Int = 0): List<Event> = suspendCancellableCoroutine { continuation ->
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
            val nextDate = LocalDate.parse(event.lastTime).plusDays(event.days.toLong())
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


}
