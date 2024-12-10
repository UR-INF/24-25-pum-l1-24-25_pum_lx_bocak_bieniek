package com.focuszone.domain

import android.content.Context
import android.content.pm.ApplicationInfo
import com.focuszone.data.preferences.PreferencesManager
import com.focuszone.data.preferences.entities.LimitedAppEntity

class AppManager(context: Context) {

    private val preferencesManager = PreferencesManager(context)

    /**
     * Adds or updates an app in the list of limited apps.
     * @param app The app to add or update.
     * @return true if the operation was successful.
     */
    fun addOrUpdateLimitedApp(app: LimitedAppEntity): Boolean {
        return try {
            preferencesManager.addOrUpdateLimitedApp(app)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Removes an app from the list of limited apps.
     * @param appId The ID (package name) of the app to remove.
     * @return true if the operation was successful.
     */
    fun removeLimitedApp(appId: String): Boolean {
        return try {
            preferencesManager.removeLimitedApp(appId)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Retrieves a list of all limited apps.
     * @return A list of `LimitedAppEntity` objects.
     */
    fun getAllLimitedApps(): List<LimitedAppEntity> {
        return preferencesManager.getLimitedApps()
    }

    /**
     * Retrieves a specific app by its ID (package name).
     * @param appId The ID of the app to retrieve.
     * @return The corresponding `LimitedAppEntity`, or null if not found.
     */
    fun getLimitedAppById(appId: String): LimitedAppEntity? {
        return preferencesManager.getLimitedApps().find { it.id == appId }
    }

    /** Retrieves list of all installed packaged in given context
     * @param context The context of app runtime system
     * @return List of all installed app Info
     * **/
    fun getAllInstalledApps(context: Context): List<ApplicationInfo> {
        val packageManager = context.packageManager
        return packageManager.getInstalledApplications(0)
    }

    /** Usage example
     * val installedApps = getAllInstalledApps(this)
     * installedApps.forEach { app ->
     *     val name = app.loadLabel(packageManager).toString()
     *     val icon = app.loadIcon(packageManager)
     *     println("Aplikacja: $name")
     *     etc.
     * }
     * **/

    /** 2nd iteration with only user apps
     * fun getUserInstalledApps(context: Context): List<ApplicationInfo> {
     *     val packageManager = context.packageManager
     *     return packageManager.getInstalledApplications(0).filter { app ->
     *         (app.flags and ApplicationInfo.FLAG_SYSTEM) == 0
     *     }
     * }
     * **/
}
