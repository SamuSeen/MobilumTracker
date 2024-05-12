package com.example.mobilumtracker.ui.event

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.mobilumtracker.R
import com.example.mobilumtracker.db.Running
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class EventFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event, container, false)

        // Initialize RecyclerView and adapter
        recyclerView = view.findViewById(R.id.recyclerViewEvents)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventAdapter = EventAdapter(emptyList()) // Initially, empty list
        recyclerView.adapter = eventAdapter

        // Load events data from database and update adapter
        loadEvents()

        return view
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun loadEvents() {
        // Call getEvents function to retrieve list of events
        lifecycleScope.launch(Dispatchers.Main) {
            val events = Running.getEvents() // Sort by default criteria
            eventAdapter.events = events // Update adapter with new events data
            eventAdapter.notifyDataSetChanged() // Notify adapter of data change
        }
    }
}