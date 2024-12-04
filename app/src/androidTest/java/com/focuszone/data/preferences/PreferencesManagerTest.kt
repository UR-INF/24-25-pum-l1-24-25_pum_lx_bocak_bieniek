package com.focuszone.data.preferences

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.focuszone.data.preferences.entities.LimitedAppEntity
import com.focuszone.util.Constants.DEFAULT_MESSAGE
import com.focuszone.util.Constants.SHARED_PREF_NAME

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before


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
    fun `setting user message sets correctly new message`(){
        val newMessage = DEFAULT_MESSAGE

        preferencesManager.saveUserMessage(newMessage)

        val currentMessage = preferencesManager.getUserMessage()

        assertSame(currentMessage, newMessage)
    }


    @Test
    fun `get limited apps returns empty list when no apps saved`() {
        val apps = preferencesManager.getLimitedApps()

        assertTrue(apps.isEmpty())
    }

    @Test
    fun `get limited apps returns correct list after adding apps`() {
        val testApps = listOf(
            LimitedAppEntity(
                id = "app1",
                isLimitSet = true,
                limitMinutes = 30,
                isSessionsSet = false,
                numberOfSessions = null,
                sessionMinutes = null
            ),
            LimitedAppEntity(
                id = "app2",
                isLimitSet = true,
                limitMinutes = 45,
                isSessionsSet = true,
                numberOfSessions = 3,
                sessionMinutes = 15
            )
        )

        testApps.forEach { preferencesManager.addOrUpdateLimitedApp(it) }

        val retrievedApps = preferencesManager.getLimitedApps()

        assertEquals(testApps.size, retrievedApps.size)
        testApps.forEachIndexed { index, originalApp ->
            assertEquals(originalApp.id, retrievedApps[index].id)
            assertEquals(originalApp.isLimitSet, retrievedApps[index].isLimitSet)
            assertEquals(originalApp.limitMinutes, retrievedApps[index].limitMinutes)
        }
    }

    @Test
    fun `get limited apps returns correct list after removing app`() {
        val testApps = listOf(
            LimitedAppEntity(
                id = "app1",
                isLimitSet = true,
                limitMinutes = 30,
                isSessionsSet = false,
                numberOfSessions = null,
                sessionMinutes = null
            ),
            LimitedAppEntity(
                id = "app2",
                isLimitSet = true,
                limitMinutes = 45,
                isSessionsSet = true,
                numberOfSessions = 3,
                sessionMinutes = 15
            )
        )

        testApps.forEach { preferencesManager.addOrUpdateLimitedApp(it) }
        preferencesManager.removeLimitedApp("app1")

        val retrievedApps = preferencesManager.getLimitedApps()

        assertEquals(1, retrievedApps.size)
        assertEquals("app2", retrievedApps[0].id)
    }

    @Test
    fun `get limited apps returns correct list after updating app`() {
        val originalApp = LimitedAppEntity(
            id = "app1",
            isLimitSet = true,
            limitMinutes = 30,
            isSessionsSet = false,
            numberOfSessions = null,
            sessionMinutes = null
        )

        preferencesManager.addOrUpdateLimitedApp(originalApp)

        val updatedApp = originalApp.copy(limitMinutes = 45)
        preferencesManager.addOrUpdateLimitedApp(updatedApp)

        val retrievedApps = preferencesManager.getLimitedApps()

        assertEquals(1, retrievedApps.size)
        assertEquals(45, retrievedApps[0].limitMinutes)
    }

    @Test
    fun `get limited apps preserves all details of complex app entity`() {
        val complexApp = LimitedAppEntity(
            id = "complex_app",
            isLimitSet = true,
            limitMinutes = 60,
            isSessionsSet = true,
            numberOfSessions = 5,
            sessionMinutes = 20
        )

        preferencesManager.addOrUpdateLimitedApp(complexApp)

        val retrievedApps = preferencesManager.getLimitedApps()

        assertEquals(1, retrievedApps.size)
        val retrievedApp = retrievedApps[0]

        assertEquals(complexApp.id, retrievedApp.id)
        assertEquals(complexApp.isLimitSet, retrievedApp.isLimitSet)
        assertEquals(complexApp.limitMinutes, retrievedApp.limitMinutes)
        assertEquals(complexApp.isSessionsSet, retrievedApp.isSessionsSet)
        assertEquals(complexApp.numberOfSessions, retrievedApp.numberOfSessions)
        assertEquals(complexApp.sessionMinutes, retrievedApp.sessionMinutes)
    }

    @Test
    fun `updating existing limit sets new limit`() {
        val initialLimit = 5
        val appId = "com.test.app"
        val limitedApp = LimitedAppEntity(appId, true, false, initialLimit, null, null)

        preferencesManager.addOrUpdateLimitedApp(limitedApp)

        val newLimit = 69
        val limitedAppNewLimit = LimitedAppEntity(appId, true, false, newLimit, null, null)

        preferencesManager.addOrUpdateLimitedApp(limitedAppNewLimit)

        val fetchedAppLimit = preferencesManager.getLimitedApps().find { it.id == appId }?.limitMinutes?.toInt()

        assertEquals(newLimit, fetchedAppLimit)
    }

    @Test
    fun `add new valid limited app successfully`() {
        val validApp = LimitedAppEntity(
            id = "app1",
            isLimitSet = true,
            limitMinutes = 30,
            isSessionsSet = true,
            numberOfSessions = 3,
            sessionMinutes = 10
        )

        val result = preferencesManager.addOrUpdateLimitedApp(validApp)

        assertTrue(result)
    }

    @Test
    fun `add limited app with invalid time limit fails`() {
        val invalidApp = LimitedAppEntity(
            id = "app2",
            isLimitSet = true,
            limitMinutes = 0,
            isSessionsSet = true,
            numberOfSessions = 3,
            sessionMinutes = 10
        )

        val result = preferencesManager.addOrUpdateLimitedApp(invalidApp)

        assertFalse(result)
    }

    @Test
    fun `add limited app with invalid number of sessions fails`() {
        val invalidApp = LimitedAppEntity(
            id = "app3",
            isLimitSet = true,
            limitMinutes = 30,
            isSessionsSet = true,
            numberOfSessions = 0,
            sessionMinutes = 10
        )

        val result = preferencesManager.addOrUpdateLimitedApp(invalidApp)

        assertFalse(result)
    }

    @Test
    fun `add limited app with invalid session time fails`() {
        val invalidApp = LimitedAppEntity(
            id = "app4",
            isLimitSet = true,
            limitMinutes = 30,
            isSessionsSet = true,
            numberOfSessions = 3,
            sessionMinutes = 0
        )

        val result = preferencesManager.addOrUpdateLimitedApp(invalidApp)

        assertFalse(result)
    }

    @Test
    fun `add limited app with no limits set fails`() {
        val invalidApp = LimitedAppEntity(
            id = "app5",
            isLimitSet = false,
            limitMinutes = null,
            isSessionsSet = false,
            numberOfSessions = null,
            sessionMinutes = null
        )

        val result = preferencesManager.addOrUpdateLimitedApp(invalidApp)

        assertFalse(result)
    }

    @Test
    fun `add limited app with negative values fails`() {
        val invalidApp = LimitedAppEntity(
            id = "app7",
            isLimitSet = true,
            limitMinutes = -1,
            isSessionsSet = true,
            numberOfSessions = -1,
            sessionMinutes = -1
        )

        val result = preferencesManager.addOrUpdateLimitedApp(invalidApp)

        assertFalse(result)
    }

    @Test
    fun `add limited app with incomplete session data fails`() {
        val invalidApp = LimitedAppEntity(
            id = "app8",
            isLimitSet = true,
            limitMinutes = 30,
            isSessionsSet = true,
            numberOfSessions = 3,
            sessionMinutes = null
        )

        val result = preferencesManager.addOrUpdateLimitedApp(invalidApp)

        assertFalse(result)
    }

    @Test
    fun `remove non-existing app returns false`() {
        val result = preferencesManager.removeLimitedApp("non_existing_app")

        assertFalse(result)
    }

    @Test
    fun `remove existing app returns true`() {
        val testApp = LimitedAppEntity(
            id = "test_app",
            isLimitSet = true,
            limitMinutes = 30,
            isSessionsSet = false,
            numberOfSessions = null,
            sessionMinutes = null
        )

        preferencesManager.addOrUpdateLimitedApp(testApp)

        val result = preferencesManager.removeLimitedApp("test_app")

        assertTrue(result)
    }

    @Test
    fun `remove existing app reduces app list size`() {
        val apps = listOf(
            LimitedAppEntity(
                id = "app1",
                isLimitSet = true,
                limitMinutes = 30,
                isSessionsSet = false,
                numberOfSessions = null,
                sessionMinutes = null
            ),
            LimitedAppEntity(
                id = "app2",
                isLimitSet = true,
                limitMinutes = 45,
                isSessionsSet = false,
                numberOfSessions = null,
                sessionMinutes = null
            )
        )

        apps.forEach { preferencesManager.addOrUpdateLimitedApp(it) }

        val initialSize = preferencesManager.getLimitedApps().size

        preferencesManager.removeLimitedApp("app1")

        val updatedApps = preferencesManager.getLimitedApps()
        assertEquals(initialSize - 1, updatedApps.size)
    }

    @Test
    fun `remove multiple times same app works correctly`() {
        val testApp = LimitedAppEntity(
            id = "test_app",
            isLimitSet = true,
            limitMinutes = 30,
            isSessionsSet = false,
            numberOfSessions = null,
            sessionMinutes = null
        )

        preferencesManager.addOrUpdateLimitedApp(testApp)

        val firstRemoval = preferencesManager.removeLimitedApp("test_app")
        assertTrue(firstRemoval)

        val secondRemoval = preferencesManager.removeLimitedApp("test_app")
        assertFalse(secondRemoval)
    }

    @Test
    fun `remove app from empty list returns false`() {
        val result = preferencesManager.removeLimitedApp("any_app")

        assertFalse(result)
    }

    @Test
    fun `removing app preserves other apps`() {
        val apps = listOf(
            LimitedAppEntity(
                id = "app1",
                isLimitSet = true,
                limitMinutes = 30,
                isSessionsSet = false,
                numberOfSessions = null,
                sessionMinutes = null
            ),
            LimitedAppEntity(
                id = "app2",
                isLimitSet = true,
                limitMinutes = 45,
                isSessionsSet = false,
                numberOfSessions = null,
                sessionMinutes = null
            )
        )

        apps.forEach { preferencesManager.addOrUpdateLimitedApp(it) }

        preferencesManager.removeLimitedApp("app1")

        val remainingApps = preferencesManager.getLimitedApps()
        assertEquals(1, remainingApps.size)
        assertEquals("app2", remainingApps[0].id)
    }


    @Test
    fun addOrUpdateBlockedSite() {
    }

    @Test
    fun removeBlockedSite() {
    }

    @Test
    fun getBlockedSites() {
    }
}