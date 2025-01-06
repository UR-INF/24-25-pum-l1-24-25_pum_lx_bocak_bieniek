package com.focuszone.ui

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.focuszone.R
import com.focuszone.data.preferences.PreferencesManager
import com.focuszone.domain.UserAuthManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferencesManager = PreferencesManager(this)
        preferencesManager.clearAllData() // !!! clear data to test registration - to delete later

        val sharedPreferences = getSharedPreferences("AppPreferences", 0)
        val isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false)
        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val languageCode = sharedPreferences.getString("app_language", "en")
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        setContentView(R.layout.activity_main)
        val navController = findNavController(R.id.fragment)

        val userAuthManager = UserAuthManager(this)
        if (userAuthManager.isUserRegistered()) {
            navController.setGraph(R.navigation.nav_graph)
        } else {
            navController.setGraph(R.navigation.auth_nav_graph)
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setupWithNavController(navController)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, 0)
            }
        }
// Hide bottom navigation bar
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            when (destination.id) {
                R.id.welcomeFragment, R.id.registrationFragment -> {
                    bottomNavigationView.visibility = View.GONE
                }
                else -> {
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }

    }
}