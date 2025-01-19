package com.focuszone.ui

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.focuszone.R
import com.focuszone.data.preferences.PreferencesManager
import com.focuszone.domain.UserAuthManager
import com.focuszone.domain.services.app.AppMonitorService
import com.focuszone.util.PermissionQueue
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var preferencesManager: PreferencesManager
    private var isNotificationDialogShown = false
    private var isOverlayDialogShown = false
    private var isAccessibilityDialogShown = false


    private fun setupLanguage() {
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val languageCode = sharedPreferences.getString("app_language", "en") ?: "en"
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun setupTheme() {
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.fragment)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNavigationView.visibility = when (destination.id) {
                R.id.welcomeFragment, R.id.registrationFragment -> View.GONE
                else -> View.VISIBLE
            }
        }

        bottomNavigationView.setupWithNavController(navController)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupLanguage()
        setupTheme()
        setupNavigation()

        preferencesManager = PreferencesManager(this)
        val userAuthManager = UserAuthManager(this)
        val navController = findNavController(R.id.fragment)

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(
            if (userAuthManager.isUserRegistered()) {
                R.id.welcomeFragment
            } else {
                R.id.registrationFragment
            }
        )
        navController.graph = navGraph
    }

    override fun onStart() {
        super.onStart()

        val permissionQueue = PermissionQueue(this)
        permissionQueue.add { checkNotificationPermission(permissionQueue) }
        permissionQueue.add { checkOverlayPermission(permissionQueue) }
        permissionQueue.add { checkAccessibilityPermission(permissionQueue) }
    }

    override fun onResume() {
        super.onResume()

        if (isNotificationDialogShown) {
            isNotificationDialogShown = false
        }
        if (isOverlayDialogShown) {
            isAccessibilityDialogShown = false
        }
        if (isAccessibilityDialogShown) {
            isAccessibilityDialogShown = false
        }
    }

    // Additional functions
    fun isAccessibilityServiceEnabled(
        context: Context,
        service: Class<out AccessibilityService?>
    ): Boolean {
        val am = context.getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices =
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)

        for (enabledService in enabledServices) {
            val enabledServiceInfo: ServiceInfo = enabledService.resolveInfo.serviceInfo
            if (enabledServiceInfo.packageName.equals(context.packageName) && enabledServiceInfo.name.equals(
                    service.name
                )
            ) return true
        }

        return false
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    private fun checkNotificationPermission(queue: PermissionQueue) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            if (!isNotificationDialogShown) {
                isNotificationDialogShown = true
                AlertDialog.Builder(this)
                    .setTitle("Enable Notifications")
                    .setMessage("This app requires notification permissions. Please enable them.")
                    .setPositiveButton("Grant") { dialog, _ ->
                        requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Deny") { dialog, _ ->
                        Toast.makeText(this, "Notification permissions denied.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .setOnDismissListener {
                        isNotificationDialogShown = false // Reset flagi po zamknięciu
                        queue.onTaskComplete()
                    }
                    .show()
            }
        } else {
            queue.onTaskComplete()
        }
    }

    private fun checkOverlayPermission(queue: PermissionQueue) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            if (!isOverlayDialogShown) {
                isOverlayDialogShown = true
                AlertDialog.Builder(this)
                    .setTitle("Enable Overlay Permissions")
                    .setMessage("This app requires overlay permissions. Please enable them.")
                    .setPositiveButton("Grant") { dialog, _ ->
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:$packageName")
                        )
                        startActivity(intent)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Deny") { dialog, _ ->
                        Toast.makeText(this, "Overlay permissions denied.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .setOnDismissListener {
                        isOverlayDialogShown = false // Reset flagi po zamknięciu
                        queue.onTaskComplete()
                    }
                    .show()
            }
        } else {
            queue.onTaskComplete()
        }
    }

    private fun checkAccessibilityPermission(queue: PermissionQueue) {
        if (!isAccessibilityServiceEnabled(this, AppMonitorService::class.java)) {
            if (!isAccessibilityDialogShown) {
                isAccessibilityDialogShown = true
                AlertDialog.Builder(this)
                    .setTitle("Enable Accessibility Service")
                    .setMessage("To monitor apps, enable the Accessibility Service.")
                    .setPositiveButton("Grant") { dialog, _ ->
                        openAccessibilitySettings()
                        dialog.dismiss()
                    }
                    .setNegativeButton("Deny") { dialog, _ ->
                        Toast.makeText(this, "Accessibility permissions denied.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .setOnDismissListener {
                        isAccessibilityDialogShown = false // Reset flagi po zamknięciu
                        queue.onTaskComplete()
                    }
                    .show()
            }
        } else {
            queue.onTaskComplete()
        }
    }
}