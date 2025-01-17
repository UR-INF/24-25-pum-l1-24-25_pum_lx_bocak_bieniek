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
import com.focuszone.data.preferences.entities.BlockedApp
import com.focuszone.domain.UserAuthManager
import com.focuszone.domain.services.app.AppMonitorService
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var preferencesManager: PreferencesManager

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

    private fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, 0)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupLanguage()
        setupTheme()
        setupNavigation()
        checkOverlayPermission()

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

        checkNotificationPermission()

        //TODO translate
        if (preferencesManager.hasLimitedApps()) {
            if (!isAccessibilityServiceEnabled(context = this, service = AppMonitorService::class.java)) {
                showAccessibilityAlert()
            } else {
                startService(Intent(this, AppMonitorService::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startAppMonitorServiceIfNeeded()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

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

    private fun showAccessibilityAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enable Accessibility Service")
            .setMessage("To enable monitoring of app usage, please enable Accessibility for this app. Do you want to open settings?")
            .setPositiveButton("Yes") { dialog, _ ->
                openAccessibilitySettings()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                Toast.makeText(this, "The app cannot function without Accessibility enabled.", Toast.LENGTH_SHORT).show()
                finish()
                dialog.dismiss()
            }
            .show()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }

    // Start/stop service after adding app
    // Example:
    //
    // (activity as? MainActivity)?.startAppMonitorServiceIfNeeded()
    fun startAppMonitorServiceIfNeeded() {
        if (preferencesManager.hasLimitedApps()) {
            if (!isAccessibilityServiceEnabled(this, AppMonitorService::class.java)) {
                showAccessibilityAlert()
            } else {
                startService(Intent(this, AppMonitorService::class.java))
            }
        } else {
            stopService(Intent(this, AppMonitorService::class.java))
        }
    }
}