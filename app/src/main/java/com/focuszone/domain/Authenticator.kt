package com.focuszone.domain

// Class for authentication logic - PIN/Biometric
object Authenticator {


    /* Validate pin from existing source
    * input not is valid if...
    * ... given input contains any character different than numbers
    * ... given input is not equal to stored PIN
    * ... given input is longer than 4
    * ... given input is shorter than 4
    * ... given input is empty
    * ... given input is <= 0
    * */
    fun validatePin(userInput: String, validPin: String): Boolean {
        // check if input is empty or contains characters different than numbers
        if (userInput.isEmpty() || !userInput.all { it.isDigit() }) {
            return false
        }

        // check input length
        if (userInput.length != 4) {
            return false
        }

        // check if input is valid
        return userInput == validPin
    }
}