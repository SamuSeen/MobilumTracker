@file:Suppress("unused", "unused")

package com.example.mobilumtracker.db

import android.annotation.SuppressLint
import android.content.Context
import com.example.mobilumtracker.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

object Running {
    //to jest uruchamiane na application context nie activity context
    @SuppressLint("StaticFieldLeak")
    lateinit var dataProcessor: DataProcessor
    private var scopeDatabase: CoroutineScope? = null
    private var mileage by Delegates.notNull<Int>()

    val isInitialized: Boolean
        get() = Running::dataProcessor.isInitialized

    fun init(context: Context, coroutineScope: CoroutineScope) {
        scopeDatabase = coroutineScope
        dataProcessor = DataProcessor(context, coroutineScope, Config.DB_NAME.value.toString())
        coroutineScope.launch {
            mileage= dataProcessor.getMileage(0)
        }
    }


}
