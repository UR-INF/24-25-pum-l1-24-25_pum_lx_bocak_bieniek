package com.focuszone.ui.fragments

import androidx.fragment.app.Fragment
import com.focuszone.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.focuszone.domain.UserAuthManager

class RegisterFragment : Fragment(R.layout.fragment_registration) {

    private lateinit var userAuthManager: UserAuthManager
    private lateinit var editPIN1: EditText
    private lateinit var editPIN2: EditText
    private lateinit var switchBiometric: Switch
    private lateinit var bttnRegistration: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_registration, container, false)

        userAuthManager = UserAuthManager(requireContext())

        editPIN1 = view.findViewById(R.id.editPIN1)
        editPIN2 = view.findViewById(R.id.editPIN2)
        switchBiometric = view.findViewById(R.id.switchBiometric)
        bttnRegistration = view.findViewById(R.id.bttnRegistration)

        bttnRegistration.setOnClickListener { handleRegistration() }

        return view
    }

    private fun handleRegistration() {
        val pin1 = editPIN1.text.toString()
        val pin2 = editPIN2.text.toString()


        if (pin1.isEmpty() || pin2.isEmpty()) {
            Toast.makeText(requireContext(), "error empty pin", Toast.LENGTH_SHORT).show()
            return
        }

        if (pin1 != pin2) {
            Toast.makeText(requireContext(), "error_pin_mismatch", Toast.LENGTH_SHORT).show()
            return
        }

        val isRegistered = userAuthManager.registerUser(pin1)
        if (isRegistered) {
            Toast.makeText(requireContext(), "success_registration", Toast.LENGTH_SHORT).show()
            val navController = findNavController(this)
            navController.setGraph(R.navigation.nav_graph)
            navController.navigate(R.id.homeFragment)

            navController.setGraph(R.navigation.nav_graph)
        } else {
            Toast.makeText(requireContext(), "error_invalid_pin", Toast.LENGTH_SHORT).show()
        }
    }
}