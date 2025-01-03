package com.focuszone.domain.services.app

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.focuszone.data.preferences.PreferencesManager
import com.focuszone.data.preferences.entities.BlockedApp

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
    private var monitoredApps: List<BlockedApp> = emptyList()
    private var activeAppStartTime: Long = 0
    private var lastActivePackage: String? = null

    // on creation of this service set listener for changes in sharedPreferences
    override fun onCreate() {
        super.onCreate()
        preferencesManager = PreferencesManager(this)

        monitoredApps = preferencesManager.getLimitedApps()

        preferencesManager.addLimitedAppsChangedListener(object : PreferencesManager.OnLimitedAppsChangedListener {
            override fun onLimitedAppsChanged(newLimitedApps: List<BlockedApp>) {
                monitoredApps = newLimitedApps
            }
        })
    }

    /** On event run monitor
     * check its limit time and current usage
     * current usage is from where?
     * if current usage is >= limit blockApp()
     * monitor changes in current usage - update on add/delete
     * monitor changes in Preferences - update on add/delete
     * TODO
     * **/
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString() ?: return

        // Monitoruj aplikację, jeśli jest dodana do listy
        val app = monitoredApps.find { it.id == packageName } ?: return

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> handleAppSwitch(packageName, app)
        }
    }

    override fun onInterrupt() {
        //TODO
    }

    /** Monitor time spent in each app BlockedApp
     * get app ID/name/package name/etc
     * if app is opened count its usage time in Foreground state
     * if app limit is stopped stop counter but remember its state
     * if app limit is exceeded block app
     * handle app switching
     * */
    private fun handleAppSwitch(packageName: String, app: BlockedApp) {
        // Save current usage time after user switches apps
        if (lastActivePackage != null && lastActivePackage != packageName) {
            val elapsedTime = (System.currentTimeMillis() - activeAppStartTime) / (1000 * 60)
            preferencesManager.updateAppUsage(
                appId = lastActivePackage!!,
                timeIncrement = elapsedTime.toInt(),
                sessionIncrement = 1
            )
            activeAppStartTime = 0
        }

        // set new active app
        lastActivePackage = packageName
        activeAppStartTime = System.currentTimeMillis()

        // Check if app exceeded time
        val (currentTimeUsage, _) = preferencesManager.getAppUsage(packageName)
        if ((currentTimeUsage ?: 0) >= (app.limitMinutes ?: Int.MAX_VALUE)) {
            blockApp(packageName)
        }
    }

    /** Block given app to the end of the day
     * get app ID/name/package name/etc
     * show fullscreen message
     * tell system to go back to previous screen
    * */
    fun blockApp(packageName: String){
        Toast.makeText(this, "Aplikacja $packageName została zablokowana", Toast.LENGTH_LONG).show()

        performGlobalAction(GLOBAL_ACTION_BACK)

        // Show notification TODO
        // Show fullscreen message TODO
    }

    override fun onDestroy() {
        // Remove listener
        preferencesManager.removeLimitedAppsChangedListener(object : PreferencesManager.OnLimitedAppsChangedListener {
            override fun onLimitedAppsChanged(newLimitedApps: List<BlockedApp>) {
                // Do nothing
            }
        })
        super.onDestroy()
    }
}