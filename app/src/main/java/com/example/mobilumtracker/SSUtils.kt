package com.example.mobilumtracker

import android.content.Context
import com.example.mobilumtracker.db.Event
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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

        fun convertToLocalDate(date: Date): LocalDate {
            // Convert Date to Instant
            val instant = date.toInstant()

            // Convert Instant to ZonedDateTime with a default time zone
            val zonedDateTime = instant.atZone(ZoneId.systemDefault())

            // Extract LocalDate from ZonedDateTime
            return zonedDateTime.toLocalDate()
        }

        fun getTargetDate(event: Event): LocalDate {
            return LocalDate.parse(event.lastDate).plusDays(event.days.toLong())
        }
    }
}