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

    private val limitedAppsListener = object : PreferencesManager.OnLimitedAppsChangedListener {
        override fun onLimitedAppsChanged(newLimitedApps: List<BlockedApp>) {
            Log.d("AppMonitorService", "Listener triggered with new apps: $newLimitedApps")
            handler.post {
                monitoredApps = newLimitedApps
                Log.d("AppMonitorService", "Updated monitored apps list: $monitoredApps")

                if (monitoredApps.none { it.isLimitSet }) {
                    stopSelf()
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("AppMonitorService", "Service created")
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

    fun blockApp(packageName: String){
        Toast.makeText(this, "Aplikacja $packageName została zablokowana", Toast.LENGTH_LONG).show()
        Log.d("AppMonitorService", "Blocking app: $packageName")

        performGlobalAction(GLOBAL_ACTION_BACK)

        handler.removeCallbacksAndMessages(null)
        lastActivePackage = null

        NotificationManager(this).showBlockedAppNotification(packageName)
    }

    override fun onDestroy() {
        val listener = object : PreferencesManager.OnLimitedAppsChangedListener {
            override fun onLimitedAppsChanged(newLimitedApps: List<BlockedApp>) {
                monitoredApps = newLimitedApps
            }
        }

        try {
            preferencesManager.removeLimitedAppsChangedListener(listener)
        } catch (e: Exception) {
            Log.e("AppMonitorService", "Error removing listener", e)
        }

        handler.removeCallbacksAndMessages(null)
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

        // Najpierw dodajemy listener
        preferencesManager.addLimitedAppsChangedListener(limitedAppsListener)
        Log.d("AppMonitorService", "Added apps listener")

        // Potem inicjalizujemy listę
        monitoredApps = preferencesManager.getLimitedApps()
        Log.d("AppMonitorService", "Initial monitored apps: $monitoredApps")
    }

    // Dodaj nową metodę do restartu monitorowania
    private fun restartMonitoring() {
        handler.removeCallbacksAndMessages(null)
        // Restart monitorowania dla aktualnej aplikacji jeśli jest monitorowana
        lastActivePackage?.let { currentPackage ->
            monitoredApps.find { it.id == currentPackage }?.let { app ->
                startMonitoringApp(app)
            }
        }
    }
}