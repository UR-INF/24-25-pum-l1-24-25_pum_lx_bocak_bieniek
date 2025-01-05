package com.focuszone.domain


import com.focuszone.data.preferences.entities.BlockedApp
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ValidatorTest {

    // Test PIN input validation
    @Test
    fun `input containing any character different than number returns false`() {
        val result = Validator.isPinValid(
            userInput = "P@ssw0rd",
        )

        assertThat(result).isFalse()
    }

    @Test
    fun `correct input format returns true`() {
        val result = Validator.isPinValid(
            userInput = "9696",
        )

        assertThat(result).isTrue()
    }

    @Test
    fun `empty input returns false`() {
        val result = Validator.isPinValid(
            userInput = "",
        )

        assertThat(result).isFalse()
    }

    @Test
    fun `input longer than 4 returns false`() {
        val result = Validator.isPinValid(
            userInput = "42069",
        )

        assertThat(result).isFalse()
    }

    @Test
    fun `input shorter than 4 returns false`() {
        val result = Validator.isPinValid(
            userInput = "420",
        )

        assertThat(result).isFalse()
    }

    @Test
    fun `input lesser or equal to 0 returns false`() {
        val result = Validator.isPinValid(
            userInput = "-584",
        )

        assertThat(result).isFalse()
    }

    // Test input PIN comperasion
    val storedPin = "6969"

    @Test
    fun `inputs not equal and not valid returns false`() {
        val result = Validator.comparePins(
            firstPin = "-584",
            secondPin = storedPin
        )

        assertThat(result).isFalse()
    }

    @Test
    fun `inputs equal returns true`() {
        val result = Validator.comparePins(
            firstPin = "6969",
            secondPin = storedPin
        )

        assertThat(result).isTrue()
    }

    // Test BlockedApp validation
    @Test
    fun `validate app with valid limit and no sessions`() {
        val validApp = BlockedApp(
            id = "app1",
            isLimitSet = true,
            limitMinutes = 30,
            isSessionsSet = false,
            numberOfSessions = null,
            sessionMinutes = null,
            currentTimeUsage = null,
            currentSessionUsage = null
        )

        assertThat(Validator.validateLimitedApp(validApp)).isTrue()
    }

    @Test
    fun `validate app with zero limit minutes fails`() {
        val invalidApp = BlockedApp(
            id = "app1",
            isLimitSet = true,
            limitMinutes = 0,
            isSessionsSet = false,
            numberOfSessions = null,
            sessionMinutes = null,
            currentTimeUsage = null,
            currentSessionUsage = null
        )

        assertThat(Validator.validateLimitedApp(invalidApp)).isFalse()
    }

    @Test
    fun `validate app with null limit minutes fails`() {
        val invalidApp = BlockedApp(
            id = "app1",
            isLimitSet = true,
            limitMinutes = null,
            isSessionsSet = false,
            numberOfSessions = null,
            sessionMinutes = null,
            currentTimeUsage = null,
            currentSessionUsage = null
        )

        assertThat(Validator.validateLimitedApp(invalidApp)).isFalse()
    }

    @Test
    fun `validate app with valid sessions`() {
        val validApp = BlockedApp(
            id = "app1",
            isLimitSet = true,
            limitMinutes = 30,
            isSessionsSet = true,
            numberOfSessions = 3,
            sessionMinutes = 10,
            currentTimeUsage = null,
            currentSessionUsage = null
        )

        assertThat(Validator.validateLimitedApp(validApp)).isTrue()
    }

    @Test
    fun `validate app with zero number of sessions fails`() {
        val invalidApp = BlockedApp(
            id = "app1",
            isLimitSet = true,
            limitMinutes = 30,
            isSessionsSet = true,
            numberOfSessions = 0,
            sessionMinutes = 10,
            currentTimeUsage = null,
            currentSessionUsage = null
        )

        assertThat(Validator.validateLimitedApp(invalidApp)).isFalse()
    }

    @Test
    fun `validate app with null number of sessions fails`() {
        val invalidApp = BlockedApp(
            id = "app1",
            isLimitSet = true,
            limitMinutes = 30,
            isSessionsSet = true,
            numberOfSessions = null,
            sessionMinutes = 10,
            currentTimeUsage = null,
            currentSessionUsage = null
        )

        assertThat(Validator.validateLimitedApp(invalidApp)).isFalse()
    }

    @Test
    fun `validate app with zero session minutes fails`() {
        val invalidApp = BlockedApp(
            id = "app1",
            isLimitSet = true,
            limitMinutes = 30,
            isSessionsSet = true,
            numberOfSessions = 3,
            sessionMinutes = 0,
            currentTimeUsage = null,
            currentSessionUsage = null
        )

        assertThat(Validator.validateLimitedApp(invalidApp)).isFalse()
    }

    @Test
    fun `validate app with null session minutes fails`() {
        val invalidApp = BlockedApp(
            id = "app1",
            isLimitSet = true,
            limitMinutes = 30,
            isSessionsSet = true,
            numberOfSessions = 3,
            sessionMinutes = null,
            currentTimeUsage = null,
            currentSessionUsage = null
        )

        assertThat(Validator.validateLimitedApp(invalidApp)).isFalse()
    }

    @Test
    fun `validate app with no limits set`() {
        val validApp = BlockedApp(
            id = "app1",
            isLimitSet = false,
            limitMinutes = null,
            isSessionsSet = false,
            numberOfSessions = null,
            sessionMinutes = null,
            currentTimeUsage = null,
            currentSessionUsage = null
        )

        assertThat(Validator.validateLimitedApp(validApp)).isTrue()
    }

    @Test
    fun `validate app with negative limit minutes fails`() {
        val invalidApp = BlockedApp(
            id = "app1",
            isLimitSet = true,
            limitMinutes = -1,
            isSessionsSet = false,
            numberOfSessions = null,
            sessionMinutes = null,
            currentTimeUsage = null,
            currentSessionUsage = null
        )

        assertThat(Validator.validateLimitedApp(invalidApp)).isFalse()
    }

    @Test
    fun `validate app with negative number of sessions fails`() {
        val invalidApp = BlockedApp(
            id = "app1",
            isLimitSet = true,
            limitMinutes = 30,
            isSessionsSet = true,
            numberOfSessions = -1,
            sessionMinutes = 10,
            currentTimeUsage = null,
            currentSessionUsage = null
        )

        assertThat(Validator.validateLimitedApp(invalidApp)).isFalse()
    }

    @Test
    fun `validate app with negative session minutes fails`() {
        val invalidApp = BlockedApp(
            id = "app1",
            isLimitSet = true,
            limitMinutes = 30,
            isSessionsSet = true,
            numberOfSessions = 3,
            sessionMinutes = -1,
            currentTimeUsage = null,
            currentSessionUsage = null
        )

        assertThat(Validator.validateLimitedApp(invalidApp)).isFalse()
    }
}