package com.focuszone.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.focuszone.R
import com.focuszone.databinding.FragmentSettingsBinding

class WelcomeFragment : Fragment(R.layout.fragment_welcome){
    private lateinit var binding: FragmentSettingsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bttnLogin = view.findViewById<Button>(R.id.bttnLogin)
        bttnLogin.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

    }
}