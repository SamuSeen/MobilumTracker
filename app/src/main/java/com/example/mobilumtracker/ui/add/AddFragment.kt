package com.example.mobilumtracker.ui.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.mobilumtracker.R
import com.example.mobilumtracker.databinding.FragmentAddBinding
import com.example.mobilumtracker.db.Event
import com.example.mobilumtracker.db.Running
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddFragment.newInstance] factory method to
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

        val eventId = arguments?.getLong("eventId", -1)
        if (eventId != null && eventId != -1L) {
            lifecycleScope.launch(Dispatchers.Main) {
                val event = Running.getEvent(eventId)
                event?.let {
                    binding.editTextEvent.setText(event.event)
                    binding.editTextDescription.setText(event.description)
                    binding.editTextDays.setText(event.days.toString())
                    binding.editTextDistance.setText(event.distance.toString())
                    binding.editTextLastDate.setText(event.lastTime)
                    binding.editTextLastDistance.setText(event.lastDistance.toString())
                }
            }
        } else {
            lifecycleScope.launch(Dispatchers.Main) {
                val mileage = Running.getMileage()
                binding.editTextLastDistance.setText(mileage)
            }
            // No arguments provided, initialize fields with default values
            initializeFields()
        }

        return rootView
    }

    private fun initializeFields() {
        binding.editTextEvent.setText("")
        binding.editTextDescription.setText("")
        binding.editTextDays.setText("")
        binding.editTextDistance.setText("")
        binding.editTextLastDate.setText("")
        binding.editTextLastDistance.setText("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
