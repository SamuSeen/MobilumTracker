package com.example.mobilumtracker

import android.content.Context
import com.example.mobilumtracker.db.Event
import com.example.mobilumtracker.db.Running
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class SSUtils {

    @Suppress("UNUSED")
    companion object {
        fun getStringResource(context: Context, resId: Int): String {
            return context.getString(resId)
        }

        fun calculateDates(lastDate: LocalDate, days: Int): List<LocalDate> {
            val dates = mutableListOf<LocalDate>()
            var currentDate = lastDate
            for (i in 1..days) {
                // Add i days to lastDate to get the calculated dates
                currentDate = currentDate.plusDays(i.toLong())
                dates.add(currentDate)
            }
            return dates
        }

        /*fun convertToJavaLocalDate(calendarDate: CalendarDate): LocalDate {
            val year = calendarDate.year
            val month = calendarDate.month
            val day = calendarDate.day

            return LocalDate.of(year, month, day)
        }*/

        fun convertDateToLocalDate(date: Date): LocalDate {
            val instant = date.toInstant()
            val zonedDateTime = instant.atZone(ZoneId.systemDefault())
            return zonedDateTime.toLocalDate()
        }

        /**
         * @return now if lastDate is empty
         */
        fun getTargetDate(event: Event): LocalDate {
            if (event.lastDate.isEmpty()) {
                return LocalDate.now()
            }
            return LocalDate.parse(event.lastDate).plusDays(event.days.toLong())
        }

        fun convertLocalDateToDate(localDate: LocalDate): Long {
            val zoneId = ZoneId.systemDefault()
            val zonedDateTime = localDate.atStartOfDay(zoneId)
            return Date.from(zonedDateTime.toInstant()).time - Date().time
        }

        fun initializeNotifications(context: Context, scope: CoroutineScope) {
            scope.launch {
                val notifications = Notifications(context)
                notifications.scheduleNotifications(Running.getEvents())
            }
        }

    }
}