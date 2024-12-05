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

class AddSiteFragment : Fragment(R.layout.fragment_add_site) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appNameEditText: EditText = view.findViewById(R.id.addSiteAddress)
        val hourPicker: NumberPicker = view.findViewById(R.id.hourPicker)
        val minutePicker: NumberPicker = view.findViewById(R.id.minutePicker)
        val addButton: Button = view.findViewById(R.id.addButton)

        hourPicker.minValue = 0
        hourPicker.maxValue = 23
        minutePicker.minValue = 0
        minutePicker.maxValue = 59

        addButton.setOnClickListener {
            val siteAddress = appNameEditText.text.toString().trim()
            val hours = hourPicker.value
            val minutes = minutePicker.value

            if (siteAddress.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter the site address", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Site Address: $siteAddress\nTime: ${hours}h ${minutes}m",
                    Toast.LENGTH_LONG
                ).show()
                findNavController().navigate(R.id.addButton)
            }
        }
    }
}