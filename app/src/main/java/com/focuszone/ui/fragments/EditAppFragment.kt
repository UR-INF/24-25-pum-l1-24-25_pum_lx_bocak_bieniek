package com.focuszone.ui.fragments

import android.content.pm.PackageManager
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
import com.focuszone.data.preferences.entities.BlockedApp
import com.focuszone.domain.AppManager
import com.focuszone.ui.MainActivity

class EditAppFragment : Fragment() {

    private lateinit var appManager: AppManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_app, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appManager = AppManager(requireContext())

        val editAppName: EditText = view.findViewById(R.id.editAppName)
        val hourPicker: NumberPicker = view.findViewById(R.id.hourPicker)
        val minutePicker: NumberPicker = view.findViewById(R.id.minutePicker)
        val saveButton: Button = view.findViewById(R.id.saveButton)

        val appId = arguments?.getString("appName") ?: "Unknown App"
        val packageManager = requireContext().packageManager
        val appName = try {
            packageManager.getApplicationLabel(packageManager.getApplicationInfo(appId, 0)).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown App"
        }
        editAppName.setText(appName)

        hourPicker.minValue = 0
        hourPicker.maxValue = 23
        minutePicker.minValue = 1
        minutePicker.maxValue = 59

        saveButton.setOnClickListener {
            val newAppName = editAppName.text.toString()
            val selectedHours = hourPicker.value
            val selectedMinutes = minutePicker.value

            if (newAppName.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter an application name!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (selectedHours == 0 && selectedMinutes == 0) {
                Toast.makeText(
                    requireContext(),
                    "Set a time limit greater than 0!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val totalLimitMinutes = (selectedHours * 60) + selectedMinutes

            val blockedApp = BlockedApp(
                id = appId,
                appName = newAppName,
                isLimitSet = true,
                limitMinutes = totalLimitMinutes,
                currentTimeUsage = 0
            )

            val isSaved = appManager.addOrUpdateLimitedApp(blockedApp)

            if (isSaved) {
                Toast.makeText(
                    requireContext(),
                    "Saved: $newAppName with limit $selectedHours h $selectedMinutes m",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().onBackPressed()

                // Toggle service
                (activity as? MainActivity)?.startAppMonitorServiceIfNeeded()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Failed to save changes!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
