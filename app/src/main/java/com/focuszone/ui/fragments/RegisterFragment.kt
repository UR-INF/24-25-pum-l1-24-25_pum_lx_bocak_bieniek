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
import androidx.biometric.BiometricManager
import androidx.navigation.NavOptions
import com.focuszone.util.BiometricConstants

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

        userAuthManager = UserAuthManager(requireContext())
        preferencesManager = PreferencesManager(requireContext())

        editPIN1 = view.findViewById(R.id.editPIN1)
        editPIN2 = view.findViewById(R.id.editPIN2)
        switchBiometric = view.findViewById(R.id.switchBiometric)
        bttnRegistration = view.findViewById(R.id.bttnRegistration)

        checkBiometricAvailability()
        bttnRegistration.setOnClickListener { handleRegistration() }

        return view
    }

    private fun checkBiometricAvailability() {
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BiometricConstants.BIOMETRIC_AUTH_TYPE)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                switchBiometric.isEnabled = true
            }
            else -> {
                switchBiometric.isEnabled = false
                switchBiometric.isChecked = false
            }
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.error))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok), null)
            .show()
    }

    private fun handleRegistration() {
        val pin1 = editPIN1.text.toString()
        val pin2 = editPIN2.text.toString()
        val isBiometricEnabled = switchBiometric.isChecked && switchBiometric.isEnabled

        if (pin1.isEmpty() || pin2.isEmpty()) {
            showErrorDialog(getString(R.string.pin_empty))
            return
        }

        if (pin1 != pin2) {
            showErrorDialog(getString(R.string.pins_do_not_match))
            return
        }

        val isRegistered = userAuthManager.registerUser(pin1)
        if (isRegistered) {
            preferencesManager.savePin(pin1)
            preferencesManager.markRegistrationComplete()
            preferencesManager.toggleBiometricEnabled(isBiometricEnabled)

            val navController = findNavController()
            navController.navigate(
                R.id.homeFragment,
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.registrationFragment, true)
                    .build()
            )

        } else {
            showErrorDialog(getString(R.string.invalid_pin_format))
        }
    }
}