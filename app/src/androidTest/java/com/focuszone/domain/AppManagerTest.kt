package com.focuszone.domain

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppManagerTest {
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val appManager = AppManager(context)

    @Test
    fun `getAllInstalledApps returns non-empty list`() {
        val installedApps = appManager.getAllInstalledApps(context)
        assertTrue("Installed apps list should not be empty", installedApps.isNotEmpty())
    }

    @Test
    fun `getAllInstalledApps returns applications with valid package names`() {
        val installedApps = appManager.getAllInstalledApps(context)

        installedApps.forEach { app ->
            assertNotNull("Package name should not be null", app.packageName)
            assertTrue("Package name should not be empty", app.packageName.isNotEmpty())
        }
    }

    @Test
    fun `getAllInstalledApps returns applications with valid flags`() {
        val installedApps = appManager.getAllInstalledApps(context)

        installedApps.forEach { app ->
            // Sprawdź, czy aplikacja ma prawidłowe flagi
            assertTrue(
                "Application should have valid flags",
                app.flags != 0
            )
        }
    }
}