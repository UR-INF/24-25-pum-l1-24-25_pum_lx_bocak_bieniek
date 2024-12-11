package com.focuszone.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.focuszone.R

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("AppPreferences", 0)

        val darkModeSwitch = view.findViewById<Switch>(R.id.switchDarkMode)

        val isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false)
        darkModeSwitch.isChecked = isDarkModeEnabled
        darkModeSwitch.text = if (isDarkModeEnabled) "Disable" else "Enable"

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                darkModeSwitch.text = "Disable"
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                darkModeSwitch.text = "Enable"
            }

            with(sharedPreferences.edit()) {
                putBoolean("dark_mode", isChecked)
                apply()
            }
        }

        val bttnAuthorization = view.findViewById<Button>(R.id.bttnAuthorization)
        bttnAuthorization.setOnClickListener {
            findNavController().navigate(R.id.AuthorizationFragment)
        }
        val bttnDisableBlocks = view.findViewById<Button>(R.id.bttnDisableAllBlocks)
        bttnDisableBlocks.setOnClickListener {
            findNavController().navigate(R.id.DisableBlocksFragment)
        }
        val bttnCustomMessage = view.findViewById<Button>(R.id.bttnCustomMessage)
        bttnCustomMessage.setOnClickListener {
            findNavController().navigate(R.id.CustomMessageFragment)
        }
        val bttnAbout = view.findViewById<Button>(R.id.bttnAbout)
        bttnAbout.setOnClickListener {
            findNavController().navigate(R.id.AboutFragment)
        }
    }
}
