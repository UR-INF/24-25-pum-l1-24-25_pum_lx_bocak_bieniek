package com.focuszone.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.focuszone.R
import com.focuszone.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bttnPreferences = view.findViewById<Button>(R.id.bttnPreferences)
        bttnPreferences.setOnClickListener {
            findNavController().navigate(R.id.PreferencesFragment)
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
