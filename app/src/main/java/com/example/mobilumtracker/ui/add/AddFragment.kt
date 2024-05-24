package com.example.mobilumtracker.ui.add

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.mobilumtracker.SSUtils
import com.example.mobilumtracker.databinding.FragmentAddBinding
import com.example.mobilumtracker.db.Event
import com.example.mobilumtracker.db.Running
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        val rootView = binding.root

        val eventId = arguments?.getLong("eventId", -1) ?: -1
        if (eventId != -1L) {
            lifecycleScope.launch(Dispatchers.Main) {
                val event = Running.getEvent(eventId)
                event?.let {
                    populateEventDetails(it)
                }
            }
        } else {
            initializeFields()
        }

        setupToggleButton()
        setupDeleteButton(eventId)
        setupTriggerButton(eventId)

        return rootView
    }

    private fun populateEventDetails(event: Event) {
        binding.editTextEvent.setText(event.event)
        binding.editTextDescription.setText(event.description)
        binding.editTextDays.setText(event.days.toString())
        binding.editTextDistance.setText(event.distance.toString())
        binding.editTextLastDate.setText(event.lastDate)
        binding.editTextLastDistance.setText(event.lastDistance.toString())
    }

    private fun setupToggleButton() {
        binding.toggleTrigger.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                enableEditing(true)
            } else {
                saveEvent()
                context?.let { SSUtils.initializeNotifications(it,lifecycleScope) }
                enableEditing(false)
            }
        }
    }

    private fun setupDeleteButton(eventId: Long) {
        binding.buttonDelete.setOnClickListener {
            if (eventId != -1L) {
                showDeleteConfirmationDialog(eventId)
            } else {
                Toast.makeText(context, "No event to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmationDialog(eventId: Long) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Event")
            .setMessage("Are you sure you want to delete this event?")
            .setPositiveButton("Yes") { _, _ ->
                deleteEvent(eventId)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun setupTriggerButton(eventId: Long) {
        binding.buttonTrigger.setOnClickListener {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val mileage = Running.getMileage()
            binding.editTextLastDate.setText(today)
            binding.editTextLastDistance.setText(mileage.toString())
            if (eventId != -1L) {
                saveEvent()
            }
        }
    }

    private fun enableEditing(state: Boolean) {
        binding.editTextEvent.isEnabled = state
        binding.editTextDescription.isEnabled = state
        binding.editTextDays.isEnabled = state
        binding.editTextDistance.isEnabled = state
        binding.editTextLastDate.isEnabled = state
        binding.editTextLastDistance.isEnabled = state
    }

    private fun saveEvent() {
        val event = Event(
            event = binding.editTextEvent.text.toString(),
            description = binding.editTextDescription.text.toString(),
            days = binding.editTextDays.text.toString().toIntOrNull() ?: 0,
            distance = binding.editTextDistance.text.toString().toIntOrNull() ?: 0,
            lastDate = binding.editTextLastDate.text.toString(),
            lastDistance = binding.editTextLastDistance.text.toString().toIntOrNull() ?: 0,
            id = arguments?.getLong("eventId") ?: 0L
        )
        lifecycleScope.launch(Dispatchers.IO) {
            if (event.id == 0L) {
                Running.addEvent(event.event, event.description, event.days, event.distance, event.lastDate, event.lastDistance)
            } else {
                Running.updateEvent(event)
            }
        }
    }

    private fun deleteEvent(eventId: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            Running.deleteEvent(eventId)
        }
        activity?.runOnUiThread {
            Toast.makeText(context, "Event deleted", Toast.LENGTH_SHORT).show()
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initializeFields() {
        binding.editTextEvent.setText("")
        binding.editTextDescription.setText("")
        binding.editTextDays.setText("")
        binding.editTextDistance.setText("")
        binding.editTextLastDate.setText("")
        binding.editTextLastDistance.setText("")
        enableEditing(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

