package com.focuszone.domain

import com.focuszone.data.preferences.entities.LimitedAppEntity

// Class for authentication logic - PIN/Biometric
object Validator {

    /** Validate pin from existing source
    * input is not valid if...
    * ... given input contains any character different than numbers
    * ... given input is longer than 4
    * ... given input is shorter than 4
    * ... given input is empty
    * ... given input is <= 0
    * */
    fun isPinValid(userInput: String): Boolean {
        // check if input is empty or contains characters different than numbers
        if (userInput.isEmpty() || !userInput.all { it.isDigit() }) {
            return false
        }

        // check input length
        if (userInput.length != 4) {
            return false
        }

        return true
    }

    /** Compare PINs
    *  input is not valid if...
    *  ... does not complain with PIN validate function
    *  ... is not same as stored PIN
    * */
    fun comparePins(firstPin: String, secondPin: String): Boolean {
        return firstPin == secondPin && isPinValid(firstPin)
    }

    /** Validate LimitedAppEntity for consistency */
    fun validateLimitedApp(app: LimitedAppEntity): Boolean {
        if (app.isLimitSet) {
            if (app.limitMinutes == null || app.limitMinutes.toInt() <= 0) {
                return false
            }
        }

        if (app.isSessionsSet) {
            val numberOfSessions = app.numberOfSessions?.toInt()
            val sessionMinutes = app.sessionMinutes?.toInt()
            if (numberOfSessions == null || numberOfSessions <= 0 ||
                sessionMinutes == null || sessionMinutes <= 0
            ) {
                return false
            }
        }

        return true
    }
}