package com.focuszone.network

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ServiceTestRule
import com.focuszone.data.preferences.PreferencesManager
import com.focuszone.data.preferences.entities.BlockedSiteEntity
import com.focuszone.util.Constants.SHARED_PREF_NAME
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import java.nio.ByteBuffer

@RunWith(AndroidJUnit4::class)
class SiteBlockerTest {

    @get:Rule
    val serviceRule = ServiceTestRule()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val preferencesManager = PreferencesManager(context)

    @Before
    fun resetPreferences() {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

    @Test
    fun `test service starts successfully`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val serviceIntent = Intent(context, SiteBlocker::class.java)

        val binder = serviceRule.bindService(serviceIntent)
        assertNull("Service should not be bound", binder)
    }

    @Test
    fun `test service stops successfully`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val serviceIntent = Intent(context, SiteBlocker::class.java)

        val service = serviceRule.startService(serviceIntent)
        assertNotNull("Service should start", service)

        serviceRule.unbindService()
        // No specific assert - ensure no exceptions thrown
    }

//    @Test
//    fun `test blocked sites update dynamically`() {
//        // Step 1: Add initial blocked site
//        val initialBlockedSites = listOf(BlockedSiteEntity(url = "https://example.com"))
//        initialBlockedSites.forEach { preferencesManager.addOrUpdateBlockedSite(it) }
//
//        // Step 2: Verify initial state
//        val service = SiteBlocker()
//        service.onCreate() // Initialize the service
//        assertEquals("Initially blocked sites should match", initialBlockedSites, service.blockedSites)
//
//        // Step 3: Add new blocked site
//        val updatedBlockedSites = listOf(
//            BlockedSiteEntity(url = "https://example.com"),
//            BlockedSiteEntity(url = "https://newblocked.com")
//        )
//        updatedBlockedSites.forEach { preferencesManager.addOrUpdateBlockedSite(it) }
//
//        // Step 4: Simulate dynamic update
//        service.onBlockedSitesChanged(updatedBlockedSites)
//        assertEquals("Blocked sites should update dynamically", updatedBlockedSites, service.blockedSites)
//    }

    @Test
    fun `test traffic to blocked site is intercepted`() {
        // Add blocked site to SharedPreferences
        preferencesManager.addOrUpdateBlockedSite(BlockedSiteEntity(url = "https://example.com"))

        // Initialize service
        val service = SiteBlocker()
        service.onCreate()
        service.blockedSites = preferencesManager.getBlockedSites()

        // Simulate network packet
        val fakePacket = ByteBuffer.wrap("GET / HTTP/1.1\r\nHost: example.com\r\n\r\n".toByteArray())
        val destination = service.extractUrlFromPacket(fakePacket, fakePacket.limit())

        assertTrue("Site should be blocked", service.isBlocked(destination))
    }

    @Test
    fun `test traffic to allowed site is not intercepted`() {
        // Add blocked site to SharedPreferences
        preferencesManager.addOrUpdateBlockedSite(BlockedSiteEntity(url = "https://blocked.com"))

        // Initialize service
        val service = SiteBlocker()
        service.onCreate()
        service.blockedSites = preferencesManager.getBlockedSites()

        // Simulate network packet
        val fakePacket = ByteBuffer.wrap("GET / HTTP/1.1\r\nHost: allowed.com\r\n\r\n".toByteArray())
        val destination = service.extractUrlFromPacket(fakePacket, fakePacket.limit())

        assertFalse("Site should not be blocked", service.isBlocked(destination))
    }
}
