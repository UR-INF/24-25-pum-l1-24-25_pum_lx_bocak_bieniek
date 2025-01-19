package com.focuszone.ui.fragments

import android.app.AlertDialog
import androidx.biometric.BiometricManager
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
import com.focuszone.util.BiometricConstants

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
                showErrorDialog(getString(R.string.pin_empty))
                return@setOnClickListener
            }

            if (userAuthManager.authenticateUser(inputPin)) {
                navigateToHome()
            } else {
                showErrorDialog(getString(R.string.invalid_pin))
            }
        }

        // Biometric login
        bttnLoginBiometric.setOnClickListener {
            if (preferencesManager.isBiometricEnabled() && isBiometricAvailable()) {
                showBiometricPrompt()
            } else {
                Toast.makeText(requireContext(), getString(R.string.biometic_disabled), Toast.LENGTH_SHORT).show()
                bttnLoginBiometric.isEnabled = false
            }
        }
    }

    private fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(requireContext())
        return biometricManager.canAuthenticate(BiometricConstants.BIOMETRIC_AUTH_TYPE) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.homeFragment)
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.error))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok), null)
            .show()
    }

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
                    showErrorDialog(getString(R.string.biometric_error, errString))
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(requireContext(), getString(R.string.biometric_unsuccessful), Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_log_in))
            .setSubtitle(getString(R.string.biometric_descr))
            .setNegativeButtonText(getString(R.string.cancel))
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
