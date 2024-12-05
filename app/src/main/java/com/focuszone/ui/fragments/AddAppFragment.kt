package com.focuszone.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.focuszone.R

class AddAppFragment : Fragment(R.layout.fragment_add_app) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appNameEditText: EditText = view.findViewById(R.id.addAppName)
        val hourPicker: NumberPicker = view.findViewById(R.id.hourPicker)
        val minutePicker: NumberPicker = view.findViewById(R.id.minutePicker)
        val addButton: Button = view.findViewById(R.id.addButton)

        hourPicker.minValue = 0
        hourPicker.maxValue = 23
        minutePicker.minValue = 0
        minutePicker.maxValue = 59

        addButton.setOnClickListener {
            val appName = appNameEditText.text.toString().trim()
            val hours = hourPicker.value
            val minutes = minutePicker.value

            if (appName.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter the application name", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "App Name: $appName\nTime: ${hours}h ${minutes}m",
                    Toast.LENGTH_LONG
                ).show()
                findNavController().navigate(R.id.addButton)
            }
        }
    }
}