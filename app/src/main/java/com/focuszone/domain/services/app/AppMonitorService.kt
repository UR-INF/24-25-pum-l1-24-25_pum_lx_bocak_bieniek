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
    private val pollHandler = Handler(Looper.getMainLooper())
    private val monitorHandler = Handler(Looper.getMainLooper())
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        Log.d("AppMonitorService", "Service created")
        preferencesManager = PreferencesManager(this)
        monitoredApps = preferencesManager.getLimitedApps().filter { it.isLimitSet }

        startPolling()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString()
            Log.d("AppMonitorService", "Window state changed: $packageName")

            if (packageName != null && packageName != lastActivePackage) {
                lastActivePackage = packageName
                activeAppStartTime = System.currentTimeMillis()

                // find if given package is under monitoring
                val monitoredApp = monitoredApps.find { it.id == packageName }
                Log.d("AppMonitorService", "Monitored app: $monitoredApp")
                Log.d("AppMonitorService", "Monitored apps: $monitoredApps")

                //TODO find app usage time
                //TODO monitoring and updating time of limited App via handler + updateAppUsageTime
                //TODO block app if limit is surpassed
                if (monitoredApp != null) {

                    // check if limits are set and surpassed
                    if (monitoredApp.currentTimeUsage != null &&  monitoredApp.limitMinutes != null && monitoredApp.currentTimeUsage >= monitoredApp.limitMinutes) {
                        //TODO show dialog to go in/out of app with custom message instead of just dialog
                        showUserMessageDialog()

                        Log.d("AppMonitorService", "App blocked: $packageName")
                        blockApp(monitoredApp.id)
                    }
                }
            }
        }
    }

    private fun startPolling() {
        pollHandler.postDelayed(object : Runnable {
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
                pollHandler.postDelayed(this, 5000) // Poll every 5 seconds
            }
        }, 5000)
    }

    private fun monitorApp(app: BlockedApp) {
        pollHandler.postDelayed(object : Runnable {
            override fun run() {
                Log.d("AppMonitorService", "Monitoring time spent monitored app")

                //TODO implement logic to monitor time spent in app and save it to preferences via updateAppUsageTime


                pollHandler.postDelayed(this, 5000) //
            }
        }, 5000)
    }

    private fun updateAppUsageTime(app: BlockedApp, timeSpent: Int) {
        val updatedApp = app.copy(
            currentTimeUsage = (app.currentTimeUsage ?: 0) + timeSpent
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

    override fun onInterrupt() {
        pollHandler.removeCallbacksAndMessages(null)
        Log.d("AppMonitorService", "Service interrupted")
        Toast.makeText(this, "Service interrupted", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        stopForeground(true)
        pollHandler.removeCallbacksAndMessages(null)
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