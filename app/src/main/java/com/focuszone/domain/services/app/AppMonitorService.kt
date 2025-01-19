package com.focuszone.domain.services.app

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.focuszone.R
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
        startPolling()
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
                Log.d("AppMonitorService", "Monitored apps: $monitoredApps")

                //TODO show dialog to go in/out of app
                if (monitoredApp != null) {
                    NotificationManager(this).showBlockedAppNotification(packageName)
                    performGlobalAction(GLOBAL_ACTION_HOME)
                    showUserMessageDialog()
                    Log.d("AppMonitorService", "App blocked: $packageName")
                }
            }
        }
    }

    private fun startPolling() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                Log.d("AppMonitorService", "Polling for changes in monitored apps")
                val newMonitoredApps = preferencesManager.getLimitedApps().filter { it.isLimitSet }
                if (newMonitoredApps != monitoredApps) {
                    Log.d("AppMonitorService", "Detected changes in monitored apps")
                    monitoredApps = newMonitoredApps
                    Log.d("AppMonitorService", "Updated monitored apps: $monitoredApps")
                } else {
                    Log.d("AppMonitorService", "No changes detected in monitored apps")
                }
                handler.postDelayed(this, 5000) // Poll every 5 seconds
            }
        }, 5000)
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

    private fun blockApp(packageName: String){
        Toast.makeText(this, getString(R.string.app_blocked), Toast.LENGTH_LONG).show()
        Log.d("AppMonitorService", "Blocking app: $packageName")

        performGlobalAction(GLOBAL_ACTION_HOME)

        lastActivePackage = null

        NotificationManager(this).showBlockedAppNotification(packageName)
    }

    private fun showUserMessageDialog() {
        Log.d("AppMonitorService", "Showing dialog: $monitoredApps")

        preferencesManager.getUserMessage()?.let {
            DialogHelper.showBlockingAlert(
                this,
                it
            )
        }
    }

    override fun onDestroy() {
        stopForeground(true)
        handler.removeCallbacksAndMessages(null)
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