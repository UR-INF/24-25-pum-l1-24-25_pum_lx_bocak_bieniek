package com.focuszone.network

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.focuszone.data.preferences.PreferencesManager
import com.focuszone.data.preferences.entities.BlockedSiteEntity
import com.focuszone.util.Constants.SHARED_PREF_NAME
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SiteBlockerTest {

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val preferencesManager = PreferencesManager(context)

    @Before
    fun resetPreferences() {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        val site1 = BlockedSiteEntity(url = "https://example.com")
        val site2 = BlockedSiteEntity(url = "https://facebook.com")

        preferencesManager.addOrUpdateBlockedSite(site1)
        preferencesManager.addOrUpdateBlockedSite(site2)
    }

//    TODO()
    @Test
    fun `isBlocked method returns correct result`() {
        val method = SiteBlocker::class.java.getDeclaredMethod("isBlocked", String::class.java)
        method.isAccessible = true

        val siteBlocker = SiteBlocker()

        val resultFacebook = method.invoke(siteBlocker, "facebook.com/some/path") as Boolean
        val resultGoogle = method.invoke(siteBlocker, "google.com") as Boolean

        assertTrue(resultFacebook)
        assertFalse(resultGoogle)
    }
}