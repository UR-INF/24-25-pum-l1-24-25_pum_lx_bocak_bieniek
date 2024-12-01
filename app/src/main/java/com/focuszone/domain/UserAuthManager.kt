package com.focuszone.domain

import android.content.Context
import com.focuszone.data.preferences.PreferencesManager

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

    /**
     * Toggles biometric login functionality.
     * @param enabled true to enable biometric login, false to disable.
     */
    fun toggleBiometricLogin(enabled: Boolean) {
        preferencesManager.toggleBiometricEnabled(enabled)
    }

    /**
     * Checks if biometric login is enabled.
     * @return true if biometric login is enabled, false otherwise.
     */
    fun isBiometricLoginEnabled(): Boolean {
        return preferencesManager.isBiometricEnabled()
    }

    /**
     * Updates the custom motivational message for the user.
     * @param message The custom message to save.
     */
    fun updateUserMessage(message: String) {
        preferencesManager.saveUserMessage(message)
    }

    /**
     * Retrieves the custom motivational message for the user.
     * @return The saved custom message, or null if none exists.
     */
    fun getUserMessage(): String? {
        return preferencesManager.getUserMessage()
    }
}