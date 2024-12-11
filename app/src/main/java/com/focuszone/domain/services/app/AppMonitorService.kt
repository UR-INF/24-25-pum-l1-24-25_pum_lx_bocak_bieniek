package com.focuszone.domain.services.app

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.focuszone.data.preferences.PreferencesManager

/** Monitor time spent in applications
 *
 * HOW TO USE
 *
 * On first app start:
 * val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
 * startActivity(intent)
 *
 * It will start automatically as it is now System Service
 * **/

class AppMonitorService : AccessibilityService() {

    private var preferencesManager = PreferencesManager(this)

    /** On event check if package name is in Preferences
     * check its limit time and current usage
     * current usage is from where?
     * if current usage is >= limit blockApp()
     * monitor changes in current usage - update on add/delete
     * monitor changes in Preferences - update on add/delete
     * TODO
     * **/
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // List of BlockedApp entities with package ids
        var blockedApps = preferencesManager.getLimitedApps()

        val packageName = event.packageName?.toString() ?: return

        Toast.makeText(applicationContext, "Aplikacja otwarta: $packageName", Toast.LENGTH_SHORT).show()

    }

    override fun onInterrupt() {
        //TODO
    }

    /** Block given app to the end of the day
     * get app ID/name/package name/etc
     * show fullscreen message
     * tell system to go back to previous screen
     * TODO
    * */
    fun blockApp(){

    }
}