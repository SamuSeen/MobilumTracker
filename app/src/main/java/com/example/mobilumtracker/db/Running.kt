@file:Suppress("unused", "unused")

package com.example.mobilumtracker.db

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.mobilumtracker.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.properties.Delegates

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

    suspend fun getEvents(sort: Int = 0): List<Event> = suspendCancellableCoroutine { continuation ->
        scopeDatabase?.launch {
            val events: List<Event> = dataProcessor.getEvents()
            when (sort) {
                0 -> {//do nothing
                }
                1 -> {
                    events.sortedBy { events = sortByTimeRemaining(events) }
                }
            }
            continuation.resume(events)
        }
    }
    private fun sortByTimeRemaining(events: List<Event>): List<Event> {
        return events.sortedBy { event -> event.days.toInt() }
    }

    // Sorting function for smallest distance remaining
    private fun sortByDistanceRemaining(events: List<Event>): List<Event> {
        return events.sortedBy { event -> event.distance }
    }

    // Sorting function for alphabetical sorting by event name
    private fun sortAlphabetically(events: List<Event>): List<Event> {
        return events.sortedBy { event -> event.event }
    }


}
