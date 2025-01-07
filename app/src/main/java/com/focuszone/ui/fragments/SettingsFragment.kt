package com.focuszone.ui.fragments

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.focuszone.R
import com.focuszone.domain.UserAuthManager
import java.util.Locale

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var switchBiometric: Switch
    private lateinit var buttonChangePin: Button
    private lateinit var userAuthManager: UserAuthManager

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

        switchBiometric.isChecked = userAuthManager.isBiometricEnabled()

        switchBiometric.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableBiometric()
            } else {
                disableBiometric()
            }
        }

        buttonChangePin.setOnClickListener {
            // Zaloguj użytkownika lub pokaż formularz do zmiany PINu
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

    private fun enableBiometric() {
        // Włącz biometryczne logowanie, np. odcisk palca
        // Użyj odpowiednich metod z Android API, np. BiometricPrompt
        userAuthManager.enableBiometric()
        Toast.makeText(requireContext(), "Biometric authentication enabled", Toast.LENGTH_SHORT)
            .show()
    }

    private fun disableBiometric() {
        // Wyłącz biometryczne logowanie
        userAuthManager.disableBiometric()
        Toast.makeText(requireContext(), "Biometric authentication disabled", Toast.LENGTH_SHORT)
            .show()
    }

    private fun showChangePinDialog() {
        // Wyświetl dialog do zmiany PINu (np. za pomocą fragmentu lub aktywności)
        // Możesz zapisać nowy PIN za pomocą UserAuthManager
    }
}
