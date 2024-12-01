package com.focuszone.data.preferences

import android.content.Context
import com.focuszone.util.Constants.SHARED_PREF_NAME

// Class for managing user preferences saved locally
class PreferencesManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    // Keys for saved values
    companion object {
        private const val KEY_REGISTRATION_COMPLETE = "REGISTRATION_COMPLETE"
        private const val KEY_USER_PIN = "USER_PIN"
        private const val KEY_BIOMETRIC_ENABLED = "BIOMETRIC_ENABLED"
        private const val KEY_CUSTOM_MESSAGE = "CUSTOM_MESSAGE"
    }

    // PIN functions
    fun savePin(pin: String) {
        sharedPreferences.edit().putString(KEY_USER_PIN, pin).apply()
    }
    fun getPin(): String? {
        return sharedPreferences.getString(KEY_USER_PIN, null)
    }

    // Registration functions
    fun isRegistrationComplete(): Boolean {
        return sharedPreferences.getBoolean(KEY_REGISTRATION_COMPLETE, false)
    }
    fun markRegistrationComplete() {
        sharedPreferences.edit().putBoolean(KEY_REGISTRATION_COMPLETE, true).apply()
    }

    // Biometrics functions
    fun toggleBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }
    fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    // User message functions
    fun saveUserMessage(message: String) {
        sharedPreferences.edit().putString(KEY_CUSTOM_MESSAGE, message).apply()
    }
    fun getUserMessage(): String? {
        return sharedPreferences.getString(KEY_CUSTOM_MESSAGE, null)
    }
}