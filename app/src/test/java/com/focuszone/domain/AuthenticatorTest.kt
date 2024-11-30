package com.focuszone.domain


import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AuthenticatorTest {

    // this should be fetched from datasource (DB) from userSettings
    private val exisingUserPin = "6969"

    @Test
    fun `input containing any character different than number returns false`() {
        val result = Authenticator.validatePin(
            userInput = "P@ssw0rd",
            validPin = exisingUserPin
        )

        assertThat(result).isFalse()
    }

    @Test
    fun `different input returns false`() {
        val result = Authenticator.validatePin(
            userInput = "9696",
            validPin = exisingUserPin
        )

        assertThat(result).isFalse()
    }

    @Test
    fun `empty input returns false`() {
        val result = Authenticator.validatePin(
            userInput = "",
            validPin = exisingUserPin
        )

        assertThat(result).isFalse()
    }

    @Test
    fun `input longer than 4 returns false`() {
        val result = Authenticator.validatePin(
            userInput = "42069",
            validPin = exisingUserPin
        )

        assertThat(result).isFalse()
    }

    @Test
    fun `input shorter than 4 returns false`() {
        val result = Authenticator.validatePin(
            userInput = "420",
            validPin = exisingUserPin
        )

        assertThat(result).isFalse()
    }

    @Test
    fun `input lesser or equal to 0 returns false`() {
        val result = Authenticator.validatePin(
            userInput = "-584",
            validPin = exisingUserPin
        )

        assertThat(result).isFalse()
    }

    @Test
    fun `valid and correclty repeated PIN returns true`() {
        val result = Authenticator.validatePin(
            userInput = "6969",
            validPin = exisingUserPin
        )

        assertThat(result).isTrue()
    }
}