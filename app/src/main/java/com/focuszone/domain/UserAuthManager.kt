package com.focuszone.domain

import android.content.Context
import com.focuszone.data.preferences.PreferencesManager

/** Authentication manager for user UI */
class UserAuthManager(context: Context) {

    private val preferencesManager = PreferencesManager(context)

    /**
     * Registers the user by saving the PIN and marking registration as complete.
     * @param pin The PIN provided by the user.
     * @return true if registration is successful, false otherwise.
     */
    fun registerUser(pin: String): Boolean {
        return if (Validator.isPinValid(pin)) {
            preferencesManager.savePin(pin)
            preferencesManager.markRegistrationComplete()
            true
        } else {
            false
        }
    }

    /**
     * Checks if the user is already registered.
     * @return true if registration is complete, false otherwise.
     */
    fun isUserRegistered(): Boolean {
        return preferencesManager.isRegistrationComplete()
    }

    /**
     * Authenticates the user by comparing the input PIN with the stored PIN.
     * @param inputPin The PIN provided by the user.
     * @return true if the authentication is successful, false otherwise.
     */
    fun authenticateUser(inputPin: String): Boolean {
        val storedPin = preferencesManager.getPin()
        return storedPin != null && Validator.comparePins(inputPin, storedPin)
    }

    private val biometricEnabledKey = "biometric_enabled"

    fun isBiometricEnabled(): Boolean {
        return preferencesManager.getBoolean(biometricEnabledKey, false)
    }

    fun enableBiometric() {
        preferencesManager.saveBoolean(biometricEnabledKey, true)
    }

    fun disableBiometric() {
        preferencesManager.saveBoolean(biometricEnabledKey, false)
    }

    private val sharedPreferences = context.getSharedPreferences("UserAuth", Context.MODE_PRIVATE)

    /**
     * Sets new PIN provided by user
     */
    fun setNewPin(newPin: String) {
        if (Validator.isPinValid(newPin)) {
            preferencesManager.savePin(newPin)
        } else {
            throw IllegalArgumentException("Invalid PIN format")
        }
    }
}