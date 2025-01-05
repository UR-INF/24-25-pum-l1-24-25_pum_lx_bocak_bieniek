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
    fun `validate app with zero limit minutes fails`() {
        val invalidApp = BlockedApp(
            id = "app1",
            isLimitSet = true,
            limitMinutes = 0,
            currentTimeUsage = null,
        )

        assertThat(Validator.validateLimitedApp(invalidApp)).isFalse()
    }

    @Test
    fun `validate app with null limit minutes fails`() {
        val invalidApp = BlockedApp(
            id = "app1",
            isLimitSet = true,
            limitMinutes = null,
            currentTimeUsage = null,
        )

        assertThat(Validator.validateLimitedApp(invalidApp)).isFalse()
    }

    @Test
    fun `validate app with no limits set`() {
        val validApp = BlockedApp(
            id = "app1",
            isLimitSet = false,
            limitMinutes = null,
            currentTimeUsage = null,
        )

        assertThat(Validator.validateLimitedApp(validApp)).isTrue()
    }

    @Test
    fun `validate app with negative limit minutes fails`() {
        val invalidApp = BlockedApp(
            id = "app1",
            isLimitSet = true,
            limitMinutes = -1,
            currentTimeUsage = null,
        )

        assertThat(Validator.validateLimitedApp(invalidApp)).isFalse()
    }
}