package com.focuszone.data.preferences

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.focuszone.util.Constants.DEFAULT_MESSAGE
import com.focuszone.util.Constants.SHARED_PREF_NAME

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class PreferencesManagerTest {

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val preferencesManager = PreferencesManager(context)

    @Before
    fun resetPreferences() {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

    @Test
    fun `initial app return registration false`() {
        val registrationState = preferencesManager.isRegistrationComplete()

        assertFalse(registrationState)
    }

    @Test
    fun `after user registration is complete registration return true`() {
        preferencesManager.markRegistrationComplete()

        val registrationState = preferencesManager.isRegistrationComplete()

        assertTrue(registrationState)
    }

    @Test
    fun `initial app PIN returns null`() {
        val pin = preferencesManager.getPin()

        assertSame(pin, null)
    }

    @Test
    fun `saving PIN sets correct PIN`(){
        val pin = "6969"

        preferencesManager.savePin(pin)

        val actualPin = preferencesManager.getPin()

        assertSame(pin, actualPin)
    }

    @Test
    fun `initial app biometrics returns false`() {
        val biometricsEnabled = preferencesManager.isBiometricEnabled()

        assertSame(biometricsEnabled, false)
    }

    @Test
    fun `enabling biometrics returns true`(){
        preferencesManager.toggleBiometricEnabled(true)

        val biometricsEnabled = preferencesManager.isBiometricEnabled()

        assertSame(biometricsEnabled, true)
    }

    @Test
    fun `initial user message returns null`() {
        val userMessage = preferencesManager.getUserMessage()

        assertSame(userMessage, null)
    }

    @Test
    fun `setting user message returns new message`(){
        val newMessage = DEFAULT_MESSAGE

        preferencesManager.saveUserMessage(newMessage)

        val currentMessage = preferencesManager.getUserMessage()

        assertSame(currentMessage, newMessage)
    }
}