package com.example.mobilumtracker

import android.content.Context
import android.util.Log
import com.example.mobilumtracker.db.Event
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class SSUtils {
    /**
     * @sample val greeting = getStringResource(context, R.string.greeting_message)
     */


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
    }
}