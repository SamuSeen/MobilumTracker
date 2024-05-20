package com.example.mobilumtracker.ui.event

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
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import com.example.mobilumtracker.db.Event
import kotlinx.coroutines.CoroutineScope

/**
 * A fragment representing a list of Items.
 */
class EventFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter

    private val navController by lazy { findNavController() }

    private var date: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the optional string from the arguments
        arguments?.let {
            date = it.getString("date")
        }
    }

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
        loadEvents(date ?: "")


        return view
    }


    fun loadEvents(date: String = "",scope: CoroutineScope = lifecycleScope) {
        eventAdapter.loadEvents(scope,date)
    }
}