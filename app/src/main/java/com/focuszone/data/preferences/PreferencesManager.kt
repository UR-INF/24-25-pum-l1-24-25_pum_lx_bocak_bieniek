package com.focuszone.data.preferences

import android.content.Context
import android.util.Log
import com.focuszone.data.preferences.entities.BlockedSiteEntity
import com.focuszone.data.preferences.entities.BlockedApp
import com.focuszone.domain.Validator
import com.focuszone.util.Constants
import com.focuszone.util.Constants.SHARED_PREF_NAME
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Class for managing user preferences saved locally
class PreferencesManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson() // JSON serializer
    private val prefs = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

    // Keys for saved values
    companion object {
        private const val KEY_REGISTRATION_COMPLETE = "REGISTRATION_COMPLETE"
        private const val KEY_USER_PIN = "USER_PIN"
        private const val KEY_BIOMETRIC_ENABLED = "BIOMETRIC_ENABLED"
        private const val KEY_CUSTOM_MESSAGE = "CUSTOM_MESSAGE"
        private const val KEY_LIMITED_APPS = "LIMITED_APPS"
        private const val KEY_BLOCKED_SITES = "BLOCKED_SITES"
        private const val TAG = "PreferencesManager"
    }

    // PIN functions
    fun savePin(pin: String) {
        sharedPreferences.edit().putString(KEY_USER_PIN, pin).apply()
    }
    fun getPin(): String? {
        return sharedPreferences.getString(KEY_USER_PIN, null)
    }

    // Registration functions
    fun isRegistrationComplete(): Boolean {
        return sharedPreferences.getBoolean(KEY_REGISTRATION_COMPLETE, false)
    }
    fun markRegistrationComplete() {
        sharedPreferences.edit().putBoolean(KEY_REGISTRATION_COMPLETE, true).apply()
    }

    // Biometrics functions
    fun toggleBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }
    fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    // User message functions
    fun saveUserMessage(message: String) {
        sharedPreferences.edit().putString(KEY_CUSTOM_MESSAGE, message).apply()
    }
    fun getUserMessage(): String? {
        return sharedPreferences.getString(KEY_CUSTOM_MESSAGE, Constants.DEFAULT_MESSAGE)
    }

    // Limited Apps functions
    fun addOrUpdateLimitedApp(app: BlockedApp): Boolean {
        if (!Validator.validateLimitedApp(app)) {
            return false
        }

        val apps = getLimitedApps().toMutableList()
        val existingAppIndex = apps.indexOfFirst { it.id == app.id }

        val updatedApp = if (existingAppIndex != -1) {
            val existingApp = apps[existingAppIndex]
            app.copy(
                currentTimeUsage = if (!app.isLimitSet) 0 else existingApp.currentTimeUsage,
            )
        } else {
            app.copy(currentTimeUsage = 0)
        }

        if (existingAppIndex != -1) {
            apps[existingAppIndex] = updatedApp
        } else {
            apps.add(updatedApp)
        }

        saveLimitedApps(apps)
        return true
    }
    fun removeLimitedApp(appId: String): Boolean {
        val initialSize = getLimitedApps().size
        val apps = getLimitedApps().filter { it.id != appId }

        if (apps.size < initialSize) {
            saveLimitedApps(apps)
            return true
        }

        return false
    }
    fun getLimitedApps(): List<BlockedApp> {
        val json = sharedPreferences.getString(KEY_LIMITED_APPS, null) ?: return emptyList()
        val type = object : TypeToken<List<BlockedApp>>() {}.type
        return gson.fromJson(json, type)
    }
    fun hasLimitedApps(): Boolean {
        return getLimitedApps().any { it.isLimitSet }
    }
    private fun saveLimitedApps(apps: List<BlockedApp>) {
        val json = gson.toJson(apps)
        sharedPreferences.edit().putString(KEY_LIMITED_APPS, json).apply()
    }
    fun updateAppUsage(appId: String, timeIncrement: Int) {
        val apps = getLimitedApps().toMutableList()
        val appIndex = apps.indexOfFirst { it.id == appId }

        if (appIndex != -1) {
            val app = apps[appIndex]
            val updatedApp = app.copy(
                currentTimeUsage = (app.currentTimeUsage ?: 0) + timeIncrement,
            )
            apps[appIndex] = updatedApp
            saveLimitedApps(apps)
        }
    }
    fun getCurrentAppUsage(appId: String): Int? {
        val app = getLimitedApps().find { it.id == appId }
        return app?.currentTimeUsage
    }
    fun getAppLimit(appId: String): Int? {
        val app = getLimitedApps().find { it.id == appId }
        return app?.limitMinutes
    }

    // Blocked sites functions
    fun addOrUpdateBlockedSite(site: BlockedSiteEntity): Boolean {
        if (!Validator.isBlockedSiteValid(site)) {
            return false
        }

        val sites = getBlockedSites().toMutableList()
        val existingSiteIndex = sites.indexOfFirst { it.url == site.url }

        if (existingSiteIndex != -1) {
            sites[existingSiteIndex] = site
        } else {
            sites.add(site)
        }

        saveBlockedSites(sites)

        return true
    }
    fun removeBlockedSite(url: String) {
        val sites = getBlockedSites().filter { it.url != url }
        saveBlockedSites(sites)
    }
    fun getBlockedSites(): List<BlockedSiteEntity> {
        val json = sharedPreferences.getString(KEY_BLOCKED_SITES, null) ?: return emptyList()
        val type = object : TypeToken<List<BlockedSiteEntity>>() {}.type
        return gson.fromJson(json, type)
    }
    private fun saveBlockedSites(sites: List<BlockedSiteEntity>) {
        val json = gson.toJson(sites)
        sharedPreferences.edit().putString(KEY_BLOCKED_SITES, json).apply()
    }

    // Function to test registration - to delete later
    fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }

    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun saveCustomMessage(message: String) {
        prefs.edit().putString("custom_message", message).apply()
    }

    fun getCustomMessage(): String {
        return prefs.getString("custom_message", "") ?: ""
    }
    fun removeAllAppLimits() {
        val apps = getLimitedApps().toMutableList()

        apps.forEach { it.isLimitSet = false }

        saveLimitedApps(apps)

        Log.d(TAG, "All app limits have been removed.")
    }
}