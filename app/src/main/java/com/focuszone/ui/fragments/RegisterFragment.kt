package com.focuszone.ui.fragments

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import com.focuszone.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.navigation.fragment.findNavController
import com.focuszone.domain.UserAuthManager
import com.focuszone.data.preferences.PreferencesManager
import android.widget.Toast

class RegisterFragment : Fragment(R.layout.fragment_registration) {

    private lateinit var userAuthManager: UserAuthManager
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var editPIN1: EditText
    private lateinit var editPIN2: EditText
    private lateinit var switchBiometric: Switch
    private lateinit var bttnRegistration: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_registration, container, false)

        // Manager initialization
        userAuthManager = UserAuthManager(requireContext())
        preferencesManager = PreferencesManager(requireContext())

        // View initialization
        editPIN1 = view.findViewById(R.id.editPIN1)
        editPIN2 = view.findViewById(R.id.editPIN2)
        switchBiometric = view.findViewById(R.id.switchBiometric)
        bttnRegistration = view.findViewById(R.id.bttnRegistration)

        bttnRegistration.setOnClickListener { handleRegistration() }

        return view
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun handleRegistration() {
        val pin1 = editPIN1.text.toString()
        val pin2 = editPIN2.text.toString()
        val isBiometricEnabled = switchBiometric.isChecked

        // PIN validation
        if (pin1.isEmpty() || pin2.isEmpty()) {
            showErrorDialog("PIN cannot be empty")
            return
        }

        if (pin1 != pin2) {
            showErrorDialog("PINs do not match")
            return
        }

        val isRegistered = userAuthManager.registerUser(pin1)
        if (isRegistered) {
            preferencesManager.savePin(pin1)
            preferencesManager.markRegistrationComplete()
            preferencesManager.toggleBiometricEnabled(isBiometricEnabled)

            val navController = findNavController()
            navController.navigate(R.id.homeFragment)

        } else {
            showErrorDialog("Invalid PIN format")
        }
    }
}
