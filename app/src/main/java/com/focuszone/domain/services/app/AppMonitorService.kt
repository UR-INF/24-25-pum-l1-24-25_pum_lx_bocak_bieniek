package com.focuszone.domain.services.app

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.focuszone.data.preferences.PreferencesManager
import com.focuszone.data.preferences.entities.BlockedApp
import com.focuszone.domain.NotificationManager
import com.focuszone.util.DialogHelper

class AppMonitorService : AccessibilityService() {

    private lateinit var preferencesManager: PreferencesManager
    private var monitoredApps: List<BlockedApp> = emptyList()
    private var activeAppStartTime: Long = 0
    private var lastActivePackage: String? = null
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        Log.d("AppMonitorService", "Service created")
        preferencesManager = PreferencesManager(this)
        monitoredApps = preferencesManager.getLimitedApps().filter { it.isLimitSet }
        if (monitoredApps.isEmpty()) {
            stopSelf()
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString()
            Log.d("AppMonitorService", "Window state changed: $packageName")

            if (packageName != null && packageName != lastActivePackage) {
                lastActivePackage = packageName
                activeAppStartTime = System.currentTimeMillis()

                val monitoredApp = monitoredApps.find { it.id == packageName }
                Log.d("AppMonitorService", "Monitored app: $monitoredApp")

                if (monitoredApp != null) {
                    preferencesManager.getUserMessage()?.let {
                        DialogHelper.showBlockingAlert(
                            this,
                            it
                        )
                    }
                    performGlobalAction(GLOBAL_ACTION_BACK)
                    Log.d("AppMonitorService", "App blocked: $packageName")
                }
            }
        }
    }

    private fun startMonitoringApp(app: BlockedApp) {
        handler.removeCallbacksAndMessages(null) // Clear any pending monitors
        monitorApp(app.id, app)
    }

    override fun onInterrupt() {
        handler.removeCallbacksAndMessages(null)
        Log.d("AppMonitorService", "Service interrupted")
        Toast.makeText(this, "Service interrupted", Toast.LENGTH_SHORT).show()
    }

    private fun monitorApp(packageName: String, app: BlockedApp) {
        val currentTimeUsage = app.currentTimeUsage ?: 0
        val timeSpentMinutes = (System.currentTimeMillis() - activeAppStartTime) / 1000 / 60

        if (app.isLimitSet && currentTimeUsage + timeSpentMinutes >= app.limitMinutes!!) {
            blockApp(packageName)
        } else {
            handler.postDelayed({
                monitorApp(packageName, app)
            }, 1000) // Check every second
        }
    }

    private fun updateAppUsageTime(app: BlockedApp) {
        val timeSpentMinutes = (System.currentTimeMillis() - activeAppStartTime) / 1000 / 60
        val updatedApp = app.copy(
            currentTimeUsage = (app.currentTimeUsage ?: 0) + timeSpentMinutes.toInt()
        )
        preferencesManager.addOrUpdateLimitedApp(updatedApp)
    }

    // TODO translate
    fun blockApp(packageName: String){
        Toast.makeText(this, "Aplikacja $packageName została zablokowana", Toast.LENGTH_LONG).show()
        Log.d("AppMonitorService", "Blocking app: $packageName")

        performGlobalAction(GLOBAL_ACTION_BACK)

        lastActivePackage = null

        NotificationManager(this).showBlockedAppNotification(packageName)
    }


    override fun onDestroy() {
        stopForeground(true)
        super.onDestroy()
        Log.d("AppMonitorService", "Service destroyed")
    }

    @SuppressLint("ForegroundServiceType")
    override fun onServiceConnected() {
        preferencesManager = PreferencesManager(this)
        notificationManager = NotificationManager(this)

        startForeground(1, notificationManager.showAppMonitorServiceRunningNotificationF())

        Log.d("AppMonitorService", "Service connected")

        monitoredApps = preferencesManager.getLimitedApps()
        Log.d("AppMonitorService", "Initial monitored apps: $monitoredApps")
    }
}