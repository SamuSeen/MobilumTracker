package com.example.mobilumtracker.ui.calendar

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilumtracker.SSUtils
import com.example.mobilumtracker.databinding.FragmentCalendarBinding
import com.example.mobilumtracker.db.Running
import com.example.mobilumtracker.ui.event.EventAdapter
import kotlinx.coroutines.launch
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import java.sql.Date
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class CalendarFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val rootView = binding.root

        recyclerView = binding.recyclerViewEvents
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventAdapter = EventAdapter(emptyList())
        recyclerView.adapter = eventAdapter

        val calendarView = binding.calendarView
        calendarView.onDateClickListener = { date ->
            val selectedDates = calendarView.selectedDates
            if (selectedDates.size == 1) {
                loadEventsForDate(SSUtils.convertDateToLocalDate(date.date))
            }
        }
        calendarView.onDateLongClickListener = { date ->
            //TODO Move to add fragment
            // Do something ...
        }

        calendarView.setupCalendar(
            selectionMode = CalendarView.SelectionMode.SINGLE,
            firstDayOfWeek = Calendar.MONDAY,
            showYearSelectionView = false
        )
        setDatesIndicators(calendarView)
        loadEventsForDate(LocalDate.now())
        return rootView
    }

    private fun setDatesIndicators(calendarView: CalendarView) {
        val indicators: MutableList<CalendarView.DateIndicator> = mutableListOf()
        lifecycleScope.launch {
            val events = Running.getEvents()
            for (event in events) {
                val nextDate = SSUtils.getTargetDate(event).format(DateTimeFormatter.ISO_DATE)
                indicators.add(createIndicator(nextDate, Color.RED))
                Log.d("CalendarFragment", "Added date: $nextDate")
            }
            if (indicators.isNotEmpty()) calendarView.datesIndicators = indicators.toList()
            //Log.d("CalendarFragment", "Calendar initialized with ${calendarView.datesIndicators}")
        }

    }

    private fun createIndicator(dateString: String, color: Int): CalendarView.DateIndicator {
        val date = LocalDate.parse(dateString)
        val calendarDate = CalendarDate(Date.valueOf(date.format(DateTimeFormatter.ISO_DATE)))
        return SimpleDateIndicator(calendarDate, color)
    }

    private fun loadEventsForDate(date: LocalDate) {
        lifecycleScope.launch {
            eventAdapter.loadEvents(lifecycleScope, date.format(DateTimeFormatter.ISO_LOCAL_DATE))
            //Log.d("CalendarFragment", "Selected date: ${date.format(DateTimeFormatter.ISO_LOCAL_DATE)}")
            // jest tutaj optimalizacja ale zrobię to jak będę miał czas
        }
    }




}
class SimpleDateIndicator(override val date: CalendarDate, override val color: Int) :
    CalendarView.DateIndicator

