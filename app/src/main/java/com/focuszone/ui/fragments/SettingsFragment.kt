package com.focuszone.ui.fragments

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.focuszone.R
import com.focuszone.domain.UserAuthManager
import com.focuszone.data.preferences.PreferencesManager
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import java.util.Locale

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var switchBiometric: Switch
    private lateinit var buttonChangePin: Button
    private lateinit var userAuthManager: UserAuthManager
    private lateinit var preferencesManager: PreferencesManager

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        requireContext().resources.updateConfiguration(
            config,
            requireContext().resources.displayMetrics
        )

        with(sharedPreferences.edit()) {
            putString("app_language", languageCode)
            apply()
        }

        requireActivity().recreate()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("AppPreferences", 0)
        preferencesManager = PreferencesManager(requireContext())

        val darkModeSwitch = view.findViewById<Switch>(R.id.switchDarkMode)

        val isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false)
        darkModeSwitch.isChecked = isDarkModeEnabled
        darkModeSwitch.text =
            if (isDarkModeEnabled) getString(R.string.disable) else getString(R.string.enable)

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                darkModeSwitch.text = getString(R.string.disable)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                darkModeSwitch.text = getString(R.string.enable)
            }

            with(sharedPreferences.edit()) {
                putBoolean("dark_mode", isChecked)
                apply()
            }
        }

        switchBiometric = view.findViewById(R.id.switchBiometric)
        buttonChangePin = view.findViewById(R.id.buttonChangePin)
        userAuthManager = UserAuthManager(requireContext())

        checkBiometricAvailability()


        val isBiometricEnabled = preferencesManager.isBiometricEnabled()
        switchBiometric.isChecked = isBiometricEnabled
        switchBiometric.text = if (isBiometricEnabled) getString(R.string.disable) else getString(R.string.enable)

        switchBiometric.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableBiometric()
                switchBiometric.text = getString(R.string.disable)
            } else {
                disableBiometric()
                switchBiometric.text = getString(R.string.enable)
            }
        }

        buttonChangePin.setOnClickListener {
            showChangePinDialog()
        }

        val bttnPolish = view.findViewById<Button>(R.id.bttnPolish)
        bttnPolish.setOnClickListener {
            setLocale("pl")
        }

        val bttnEnglish = view.findViewById<Button>(R.id.bttnEnglish)
        bttnEnglish.setOnClickListener {
            setLocale("en")
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

    private fun checkBiometricAvailability() {
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                switchBiometric.isEnabled = true
            }
            else -> {
                switchBiometric.isEnabled = false
                switchBiometric.isChecked = false
                preferencesManager.toggleBiometricEnabled(false)
            }
        }
    }

    private fun enableBiometric() {
        userAuthManager.enableBiometric()
        preferencesManager.toggleBiometricEnabled(true)
        Toast.makeText(requireContext(), getString(R.string.biometric_auth_enabled), Toast.LENGTH_SHORT)
            .show()
    }

    private fun disableBiometric() {
        userAuthManager.disableBiometric()
        preferencesManager.toggleBiometricEnabled(false)
        Toast.makeText(requireContext(), getString(R.string.biometric_auth_disabled), Toast.LENGTH_SHORT)
            .show()
    }

    private fun showChangePinDialog() {
        val dialogView = layoutInflater.inflate(R.layout.fragment_change_pin, null)

        val currentPinEditText = dialogView.findViewById<EditText>(R.id.editTextCurrentPin)
        val newPinEditText = dialogView.findViewById<EditText>(R.id.editTextNewPin)
        val confirmPinEditText = dialogView.findViewById<EditText>(R.id.editTextConfirmNewPin)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.change_pin))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save), null)
            .setNegativeButton(getString(R.string.cancel), null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val currentPin = currentPinEditText.text.toString()
                val newPin = newPinEditText.text.toString()
                val confirmPin = confirmPinEditText.text.toString()

                if (!userAuthManager.authenticateUser(currentPin)) {
                    Toast.makeText(requireContext(), getString(R.string.invalid_current_pin), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (newPin.isEmpty() || confirmPin.isEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.pin_empty), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (newPin != confirmPin) {
                    Toast.makeText(requireContext(), getString(R.string.pins_do_not_match), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                userAuthManager.setNewPin(newPin)
                Toast.makeText(requireContext(), getString(R.string.pin_changed_successfully), Toast.LENGTH_SHORT).show()

                dialog.dismiss()
            }
        }
        dialog.show()
    }

}
