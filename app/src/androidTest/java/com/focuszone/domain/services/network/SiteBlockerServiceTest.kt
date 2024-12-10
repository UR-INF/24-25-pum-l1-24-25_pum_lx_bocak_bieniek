package com.focuszone.domain.services.network

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.focuszone.data.preferences.PreferencesManager
import com.focuszone.data.preferences.entities.BlockedSiteEntity
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SiteBlockerServiceTest {

    private lateinit var context: Context
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var service: SiteBlockerService

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        preferencesManager = PreferencesManager(context)
    }
}