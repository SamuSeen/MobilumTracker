package com.example.mobilumtracker

import android.content.Context

class Utils {
    /**
     * @sample val greeting = getStringResource(context, R.string.greeting_message)
     */
    fun getStringResource(context: Context, resId: Int): String {
        return context.getString(resId)
    }
}