package com.example.mobilumtracker

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
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
            try {
                return LocalDate.parse(event.lastDate).plusDays(event.days.toLong())
            } catch (e: Exception) {
                e.printStackTrace()
                return LocalDate.now()
            }
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

/**
 * Date Watcher class for EditText
 * @sample DateInputMask(binding.editTextLastDate).listen()
 * Inspired by a Google dev
 */
class DateInputMask(val input : EditText) {

    fun listen() {
        input.addTextChangedListener(mDateEntryWatcher)
    }

    private val mDateEntryWatcher = object : TextWatcher {

        var edited = false
        val dividerCharacter = "/"

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (edited) {
                edited = false
                return
            }

            var working = getEditText()

            working = manageDateDivider(working, 4, start, before)
            working = manageDateDivider(working, 7, start, before)

            edited = true
            input.setText(working)
            input.setSelection(input.text.length)
        }

        /**
         * Manage the divider character
         * modified to YYYY/MM/DD format
         */
        private fun manageDateDivider(working: String, position: Int, start: Int, before: Int): String {
            if (working.length == position) {
                return if (before <= position && start < position)
                    working + dividerCharacter
                else
                    working.dropLast(1)
            }
            return working
        }

        /**
         * Get the EditText text
         * modified to YYYY/MM/DD format
         */
        private fun getEditText(): String {
            return if (input.text.length >= 10)
                input.text.toString().substring(0, 10)
            else
                input.text.toString()
        }

        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    }
}