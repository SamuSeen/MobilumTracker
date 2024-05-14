package com.example.mobilumtracker.ui.event

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import com.example.mobilumtracker.R
import com.example.mobilumtracker.databinding.ItemEventBinding
import com.example.mobilumtracker.Utils
import androidx.navigation.fragment.findNavController
import com.example.mobilumtracker.db.Event
import java.time.Duration
import java.time.LocalDate


class EventAdapter(var events: List<Event>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEventBinding.inflate(inflater, parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
        holder.itemView.setOnClickListener {
            // Navigate to the add fragment with the event ID as an argument
            val action = EventFragmentDirections.actionNavEventToNavAdd(event.id)
            it.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }

    inner class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            binding.textViewEventName.text = event.event
            binding.textViewEventDescription.text = event.description
            val nextDate = LocalDate.parse(event.lastTime).plusDays(event.days.toLong())
            val durationText =Duration.between(LocalDate.now().atStartOfDay(), nextDate.atStartOfDay()).toDays().toString()
            binding.textViewDays.text = durationText
            val distanceText = (event.lastDistance - event.distance).toString()
            binding.textViewDistance.text = distanceText
        }
    }
    //todo make events clickable
}
