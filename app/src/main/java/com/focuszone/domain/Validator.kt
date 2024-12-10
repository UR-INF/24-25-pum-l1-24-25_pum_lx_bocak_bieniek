package com.focuszone.domain

import com.focuszone.data.preferences.entities.BlockedSiteEntity
import com.focuszone.data.preferences.entities.BlockedApp

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

    /** Validate BlockedApp for consistency */
    fun validateLimitedApp(app: BlockedApp): Boolean {
        if (app.isLimitSet) {
            if (app.limitMinutes == null || app.limitMinutes.toInt() <= 0) {
                return false
            }
        }

        if (app.isSessionsSet) {
            val numberOfSessions = app.numberOfSessions
            val sessionMinutes = app.sessionMinutes
            if (numberOfSessions == null || numberOfSessions <= 0 ||
                sessionMinutes == null || sessionMinutes <= 0
            ) {
                return false
            }
        }

        return true
    }

    /**
     * Validates a URL in a BlockedSiteEntity.
     * The URL is invalid if:
     * - It is empty.
     * - It does not match the pattern of a valid URL.
     *
     * @param site The BlockedSiteEntity to validate.
     * @return true if the URL is valid, false otherwise.
     */
    fun isBlockedSiteValid(site: BlockedSiteEntity): Boolean {
        // Check if URL is empty
        if (site.url.isBlank()) {
            return false
        }

        // Basic regex for validating URLs
        val urlPattern = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$".toRegex()
        return site.url.matches(urlPattern)
    }
}