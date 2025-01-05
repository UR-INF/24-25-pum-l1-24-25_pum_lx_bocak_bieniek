package com.focuszone.domain

import com.focuszone.data.preferences.entities.BlockedSiteEntity
import com.focuszone.data.preferences.entities.BlockedApp

/** Input Validation manager for app */
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

        return true
    }

    /**
     * Validates a URL in a BlockedSiteEntity.
     * The URL is invalid if:
     * - It is empty.
     * - It does not match the pattern of a valid URL (http://www.example.com).
     *
     * @param site The BlockedSiteEntity to validate.
     * @return true if the URL is valid, false otherwise.
     */
    fun isBlockedSiteValid(site: BlockedSiteEntity): Boolean {
        // Check if URL is empty
        if (site.url.isBlank()) {
            return false
        }

        val url = site.url.trim()

        // Regex to match URLs starting with http:// or https://
        val urlPattern = "^(http|https)://www\\.[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$".toRegex()
        return url.matches(urlPattern)
    }
}