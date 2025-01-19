package com.focuszone.managers

import android.content.Context
import android.widget.Switch
import android.widget.Toast
import androidx.biometric.BiometricManager
import com.focuszone.R
import com.focuszone.data.preferences.PreferencesManager
import com.focuszone.util.BiometricConstants

class BiometricManager(
    private val context: Context,
    private val preferencesManager: PreferencesManager
) {
    fun setupBiometricSwitch(switchBiometric: Switch) {
        val isBiometricAvailable = isBiometricAvailable()
        val isEnabled = preferencesManager.isBiometricEnabled()

        switchBiometric.isEnabled = isBiometricAvailable
        switchBiometric.isChecked = isEnabled
        switchBiometric.text = if (isEnabled) context.getString(R.string.disable) else context.getString(R.string.enable)

        switchBiometric.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableBiometric()
                switchBiometric.text = context.getString(R.string.disable)
            } else {
                disableBiometric()
                switchBiometric.text = context.getString(R.string.enable)
            }
        }
    }

    private fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BiometricConstants.BIOMETRIC_AUTH_TYPE) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun enableBiometric() {
        preferencesManager.toggleBiometricEnabled(true)
        Toast.makeText(context, context.getString(R.string.biometric_auth_enabled), Toast.LENGTH_SHORT).show()
    }

    private fun disableBiometric() {
        preferencesManager.toggleBiometricEnabled(false)
        Toast.makeText(context, context.getString(R.string.biometric_auth_disabled), Toast.LENGTH_SHORT).show()
    }
}
