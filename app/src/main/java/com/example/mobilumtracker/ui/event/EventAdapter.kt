package com.example.mobilumtracker.ui.event

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilumtracker.R
import com.example.mobilumtracker.SSUtils
import com.example.mobilumtracker.databinding.ItemEventBinding
import com.example.mobilumtracker.db.Event
import com.example.mobilumtracker.db.Running
import com.example.mobilumtracker.ui.calendar.CalendarFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate


class EventAdapter(private var events: List<Event>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEventBinding.inflate(inflater, parent, false)
        return EventViewHolder(binding)
    }



    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
        holder.itemView.setOnClickListener { view ->
            val navController = view.findNavController()

            when (navController.currentDestination?.id) {
                R.id.nav_event -> {
                    val action = EventFragmentDirections.actionNavEventToNavAdd(event.id)
                    navController.navigate(action)
                }
                R.id.nav_calendar -> {
                    val action = CalendarFragmentDirections.actionNavCalendarToNavAdd(event.id)
                    navController.navigate(action)
                }
                else -> {
                    Log.e("EventAdapter", "Unexpected navigation destination: ${navController.currentDestination?.id}")
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return events.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun loadEvents(scope: CoroutineScope, date: String="") {
        scope.launch(Dispatchers.Main) {
            val events = if (date!="") {
                Running.getEvents(date)
            } else {
                Running.getEvents()
            }
            Log.d("EventAdapter", "Loaded events: $events")
            this@EventAdapter.events = events
            this@EventAdapter.notifyDataSetChanged()
        }
    }

    inner class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event,) {
            binding.textViewEventName.text = event.event
            binding.textViewEventDescription.text = event.description
            val nextDate = SSUtils.getTargetDate(event)
            val durationText =
                Duration.between(LocalDate.now().atStartOfDay(), nextDate.atStartOfDay()).toDays().toString()
            binding.textViewDays.text = durationText
            val distanceText = (event.lastDistance + event.distance - Running.getMileage()).toString()
            binding.textViewDistance.text = distanceText
        }
    }
}
