package com.focuszone.data.preferences

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.focuszone.data.preferences.entities.BlockedSiteEntity
import com.focuszone.data.preferences.entities.BlockedApp
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

    // Registration tests
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

    // PIN options
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

    // Biometrics options
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

    // User message
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

    // App limits
    @Test
    fun `get limited apps returns empty list when no apps saved`() {
        val apps = preferencesManager.getLimitedApps()

        assertTrue(apps.isEmpty())
    }

    @Test
    fun `get limited apps returns correct list after adding apps`() {
        val testApps = listOf(
            BlockedApp(
                id = "app1",
                isLimitSet = true,
                limitMinutes = 30,
                currentTimeUsage = null,
            ),
            BlockedApp(
                id = "app2",
                isLimitSet = true,
                limitMinutes = 45,
                currentTimeUsage = null,
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
            BlockedApp(
                id = "app1",
                isLimitSet = true,
                limitMinutes = 30,
                currentTimeUsage = null,
            ),
            BlockedApp(
                id = "app2",
                isLimitSet = true,
                limitMinutes = 45,
                currentTimeUsage = null,
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
        val originalApp = BlockedApp(
            id = "app1",
            isLimitSet = true,
            limitMinutes = 30,
            currentTimeUsage = null,
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
        val complexApp = BlockedApp(
            id = "complex_app",
            isLimitSet = true,
            limitMinutes = 60,
            currentTimeUsage = null,

        )

        preferencesManager.addOrUpdateLimitedApp(complexApp)

        val retrievedApps = preferencesManager.getLimitedApps()

        assertEquals(1, retrievedApps.size)
        val retrievedApp = retrievedApps[0]

        assertEquals(complexApp.id, retrievedApp.id)
        assertEquals(complexApp.isLimitSet, retrievedApp.isLimitSet)
        assertEquals(complexApp.limitMinutes, retrievedApp.limitMinutes)
    }

    @Test
    fun `updating existing limit sets new limit`() {
        val initialLimit = 5
        val appId = "com.test.app"
        val limitedApp = BlockedApp(appId, true, initialLimit, null)

        preferencesManager.addOrUpdateLimitedApp(limitedApp)

        val newLimit = 69
        val limitedAppNewLimit = BlockedApp(appId, true, newLimit, 0)

        preferencesManager.addOrUpdateLimitedApp(limitedAppNewLimit)

        val fetchedAppLimit = preferencesManager.getLimitedApps().find { it.id == appId }?.limitMinutes?.toInt()

        assertEquals(newLimit, fetchedAppLimit)
    }

    @Test
    fun `add new valid limited app successfully`() {
        val validApp = BlockedApp(
            id = "app1",
            isLimitSet = true,
            limitMinutes = 30,
            currentTimeUsage = null,
        )

        val result = preferencesManager.addOrUpdateLimitedApp(validApp)

        assertTrue(result)
    }

    @Test
    fun `add limited app with invalid time limit fails`() {
        val invalidApp = BlockedApp(
            id = "app2",
            isLimitSet = true,
            limitMinutes = 0,
            currentTimeUsage = null,
        )

        val result = preferencesManager.addOrUpdateLimitedApp(invalidApp)

        assertFalse(result)
    }

    @Test
    fun `add limited app with invalid number of sessions fails`() {
        val invalidApp = BlockedApp(
            id = "app3",
            isLimitSet = true,
            limitMinutes = 30,
            currentTimeUsage = null,
        )

        val result = preferencesManager.addOrUpdateLimitedApp(invalidApp)

        assertFalse(result)
    }

    @Test
    fun `add limited app with invalid session time fails`() {
        val invalidApp = BlockedApp(
            id = "app4",
            isLimitSet = true,
            limitMinutes = 30,
            currentTimeUsage = null,
        )

        val result = preferencesManager.addOrUpdateLimitedApp(invalidApp)

        assertFalse(result)
    }

    @Test
    fun `add limited app with no limits set fails`() {
        val invalidApp = BlockedApp(
            id = "app5",
            isLimitSet = false,
            limitMinutes = null,
            currentTimeUsage = null,
        )

        val result = preferencesManager.addOrUpdateLimitedApp(invalidApp)

        assertFalse(result)
    }

    @Test
    fun `add limited app with negative values fails`() {
        val invalidApp = BlockedApp(
            id = "app7",
            isLimitSet = true,
            limitMinutes = -1,
            currentTimeUsage = null,
        )

        val result = preferencesManager.addOrUpdateLimitedApp(invalidApp)

        assertFalse(result)
    }

    @Test
    fun `add limited app with incomplete session data fails`() {
        val invalidApp = BlockedApp(
            id = "app8",
            isLimitSet = true,
            limitMinutes = 30,
            currentTimeUsage = null,
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
        val testApp = BlockedApp(
            id = "test_app",
            isLimitSet = true,
            limitMinutes = 30,
            currentTimeUsage = null,
        )

        preferencesManager.addOrUpdateLimitedApp(testApp)

        val result = preferencesManager.removeLimitedApp("test_app")

        assertTrue(result)
    }

    @Test
    fun `remove existing app reduces app list size`() {
        val apps = listOf(
            BlockedApp(
                id = "app1",
                isLimitSet = true,
                limitMinutes = 30,
                currentTimeUsage = null,
            ),
            BlockedApp(
                id = "app2",
                isLimitSet = true,
                limitMinutes = 45,
                currentTimeUsage = null,
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
        val testApp = BlockedApp(
            id = "test_app",
            isLimitSet = true,
            limitMinutes = 30,
            currentTimeUsage = null,
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
            BlockedApp(
                id = "app1",
                isLimitSet = true,
                limitMinutes = 30,
                currentTimeUsage = null,
            ),
            BlockedApp(
                id = "app2",
                isLimitSet = true,
                limitMinutes = 45,
                currentTimeUsage = null,
            )
        )

        apps.forEach { preferencesManager.addOrUpdateLimitedApp(it) }

        preferencesManager.removeLimitedApp("app1")

        val remainingApps = preferencesManager.getLimitedApps()
        assertEquals(1, remainingApps.size)
        assertEquals("app2", remainingApps[0].id)
    }

    @Test
    fun `get app usage successfully`() {
        val app = BlockedApp(
            id = "com.example.app",
            isLimitSet = true,
            limitMinutes = 15,
            currentTimeUsage = 5,
        )

        preferencesManager.addOrUpdateLimitedApp(app)

        val timeUsage = preferencesManager.getAppUsage("com.example.app")

        assertEquals(5, timeUsage)
    }

    @Test
    fun `update usage for non-existent app does nothing`() {
        preferencesManager.updateAppUsage(
            appId = "com.nonexistent.app",
            timeIncrement = 10,
        )

        val savedApps = preferencesManager.getLimitedApps()
        assertTrue("No apps should exist", savedApps.isEmpty())
    }

    @Test
    fun `update app usage successfully`() {
        val app = BlockedApp(
            id = "com.example.app",
            isLimitSet = true,
            limitMinutes = 15,
            currentTimeUsage = 0,
        )

        preferencesManager.addOrUpdateLimitedApp(app)

        preferencesManager.updateAppUsage(
            appId = "com.example.app",
            timeIncrement = 10,
        )

        val savedApps = preferencesManager.getLimitedApps()
        val updatedApp = savedApps.find { it.id == "com.example.app" }
        assertNotNull("App should exist after update", updatedApp)
        assertEquals(10, updatedApp?.currentTimeUsage)
    }


    // Blocked site options
    @Test
    fun `adding a new blocked site saves it correctly`() {
        val site = BlockedSiteEntity(url = "https://example.com")

        preferencesManager.addOrUpdateBlockedSite(site)

        val sites = preferencesManager.getBlockedSites()
        assertTrue("Blocked sites should contain the newly added site", sites.contains(site))
    }

    @Test
    fun `adding an existing blocked site updates it correctly`() {
        val initialSite = BlockedSiteEntity(url = "https://example.com")
        val updatedSite = BlockedSiteEntity(url = "https://example.com") // You can add more fields for testing updates

        preferencesManager.addOrUpdateBlockedSite(initialSite)
        preferencesManager.addOrUpdateBlockedSite(updatedSite)

        val sites = preferencesManager.getBlockedSites()
        assertEquals("Blocked site should be updated", updatedSite, sites.find { it.url == updatedSite.url })
    }

    @Test
    fun `removing a blocked site deletes it correctly`() {
        val site1 = BlockedSiteEntity(url = "https://example1.com")
        val site2 = BlockedSiteEntity(url = "https://example2.com")

        preferencesManager.addOrUpdateBlockedSite(site1)
        preferencesManager.addOrUpdateBlockedSite(site2)
        preferencesManager.removeBlockedSite("https://example1.com")

        val sites = preferencesManager.getBlockedSites()
        assertFalse("Blocked sites should not contain the removed site", sites.any { it.url == "https://example1.com" })
        assertTrue("Blocked sites should still contain the remaining site", sites.contains(site2))
    }

    @Test
    fun `getting blocked sites returns all saved sites`() {
        val site1 = BlockedSiteEntity(url = "https://example1.com")
        val site2 = BlockedSiteEntity(url = "https://example2.com")

        preferencesManager.addOrUpdateBlockedSite(site1)
        preferencesManager.addOrUpdateBlockedSite(site2)

        val sites = preferencesManager.getBlockedSites()
        assertEquals("There should be two blocked sites", 2, sites.size)
        assertTrue("Blocked sites should contain site1", sites.contains(site1))
        assertTrue("Blocked sites should contain site2", sites.contains(site2))
    }

    @Test
    fun `getting blocked sites returns empty list if none exist`() {
        val sites = preferencesManager.getBlockedSites()

        assertTrue("Blocked sites should be empty", sites.isEmpty())
    }

    @Test
    fun `adding a site with empty URL does not save it`() {
        val site = BlockedSiteEntity(url = "")

        preferencesManager.addOrUpdateBlockedSite(site)

        val sites = preferencesManager.getBlockedSites()
        assertFalse("Blocked sites should not contain a site with an empty URL", sites.contains(site))
    }

    @Test
    fun `removing a site that does not exist does not affect other sites`() {
        val site1 = BlockedSiteEntity(url = "https://example1.com")
        val site2 = BlockedSiteEntity(url = "https://example2.com")

        preferencesManager.addOrUpdateBlockedSite(site1)
        preferencesManager.addOrUpdateBlockedSite(site2)

        preferencesManager.removeBlockedSite("https://nonexistent.com")

        val sites = preferencesManager.getBlockedSites()
        assertEquals("Number of blocked sites should not change", 2, sites.size)
        assertTrue("Blocked sites should still contain site1", sites.contains(site1))
        assertTrue("Blocked sites should still contain site2", sites.contains(site2))
    }
}