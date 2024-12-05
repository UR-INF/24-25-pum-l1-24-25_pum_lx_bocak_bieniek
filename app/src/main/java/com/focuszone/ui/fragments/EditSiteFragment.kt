package com.focuszone.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.focuszone.R

class EditSiteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_site, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editSiteAddress: EditText = view.findViewById(R.id.editSiteAddress)
        val hourPicker: NumberPicker = view.findViewById(R.id.hourPicker)
        val minutePicker: NumberPicker = view.findViewById(R.id.minutePicker)
        val saveButton: Button = view.findViewById(R.id.saveButton)

        val siteName = arguments?.getString("siteName") ?: "Unknown Site"

        editSiteAddress.setText(siteName)

        hourPicker.minValue = 0
        hourPicker.maxValue = 23
        minutePicker.minValue = 0
        minutePicker.maxValue = 59

        saveButton.setOnClickListener {
            val siteAddress = editSiteAddress.text.toString()
            val selectedHours = hourPicker.value
            val selectedMinutes = minutePicker.value

            if (siteAddress.isNotBlank()) {
                val selectedTime = "$selectedHours h $selectedMinutes m"
                Toast.makeText(
                    requireContext(),
                    "Saved: $siteAddress with limit $selectedTime",
                    Toast.LENGTH_SHORT
                ).show()

                requireActivity().onBackPressed()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please enter an address of the site!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}