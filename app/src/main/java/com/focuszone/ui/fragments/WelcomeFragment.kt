package com.focuszone.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.focuszone.R
import com.focuszone.data.preferences.PreferencesManager
import com.focuszone.domain.UserAuthManager

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    private lateinit var userAuthManager: UserAuthManager
    private lateinit var preferencesManager: PreferencesManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userAuthManager = UserAuthManager(requireContext())
        preferencesManager = PreferencesManager(requireContext())

        val editPinLogin = view.findViewById<EditText>(R.id.editTextNumber)
        val bttnLogin = view.findViewById<Button>(R.id.bttnLogin)
        val bttnLoginBiometric = view.findViewById<Button>(R.id.bttnLoginBiometric)

        // Log in with PIN code
        bttnLogin.setOnClickListener {
            val inputPin = editPinLogin.text.toString()
            if (inputPin.isBlank()) {
                showErrorDialog("PIN can not be empty.")
                return@setOnClickListener
            }

            if (userAuthManager.authenticateUser(inputPin)) {
                navigateToHome()
            } else {
                showErrorDialog("Invalid PIN. Try again")
            }
        }

        // Logowanie biometryczne
        bttnLoginBiometric.setOnClickListener {
            if (preferencesManager.isBiometricEnabled()) {
                showBiometricPrompt()
            } else {
                Toast.makeText(requireContext(), "Biometric log in is disabled.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.homeFragment)
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    // Biometric log in
    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(requireContext())
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    navigateToHome()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showErrorDialog("Biometric error: $errString")
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(requireContext(), "Unsuccessful biometric log in.", Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric log in")
            .setSubtitle("Use fingerprint to log in")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
