package com.focuszone.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.focuszone.R
import com.focuszone.data.preferences.PreferencesManager
import com.focuszone.ui.dialogs.CustomMessageDialog
import com.focuszone.utils.LocaleManager
import com.focuszone.utils.ThemeManager
import com.focuszone.managers.BiometricSettingsManager
import com.focuszone.ui.managers.ChangePinDialog
import com.focuszone.domain.UserAuthManager
import com.focuszone.ui.managers.DisableBlocksDialog

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var biometricManager: BiometricSettingsManager
    private lateinit var userAuthManager: UserAuthManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferencesManager = PreferencesManager(requireContext())
        biometricManager = BiometricSettingsManager(requireContext(), preferencesManager)
        userAuthManager = UserAuthManager(requireContext())

        val darkModeSwitch = view.findViewById<Switch>(R.id.switchDarkMode)
        val switchBiometric = view.findViewById<Switch>(R.id.switchBiometric)

        ThemeManager.setupDarkModeSwitch(requireContext(), darkModeSwitch)

        biometricManager.setupBiometricSwitch(switchBiometric)

        setupButtonListeners(view)
    }

    private fun setupButtonListeners(view: View) {
        view.findViewById<Button>(R.id.bttnPolish).setOnClickListener {
            LocaleManager.setLocale(requireActivity(), "pl")
        }
        view.findViewById<Button>(R.id.bttnEnglish).setOnClickListener {
            LocaleManager.setLocale(requireActivity(), "en")
        }
        view.findViewById<Button>(R.id.bttnDisableAllBlocks).setOnClickListener {
            DisableBlocksDialog(requireContext(), preferencesManager).show()
        }
        view.findViewById<Button>(R.id.bttnCustomMessage).setOnClickListener {
            CustomMessageDialog.show(requireContext(), preferencesManager)
        }
        view.findViewById<Button>(R.id.buttonChangePin).setOnClickListener {
            ChangePinDialog.show(requireContext(), userAuthManager)
        }
    }
}
