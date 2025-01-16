package com.focuszone.domain

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.widget.Toast
import com.focuszone.data.preferences.PreferencesManager
import com.focuszone.data.preferences.entities.BlockedApp

/** Interface for CRUD operations on applications in UI */
class AppManager(context: Context) {

    private val preferencesManager = PreferencesManager(context)

    /**
     * Adds or updates an app in the list of limited apps.
     * @param app The app to add or update.
     * @return true if the operation was successful.
     */
    fun addOrUpdateLimitedApp(app: BlockedApp): Boolean {
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
     * @return A list of `BlockedApp` objects.
     */
    fun getAllLimitedApps(): List<BlockedApp> {
        return preferencesManager.getLimitedApps()
    }

    /**
     * Retrieves a specific app by its ID (package name).
     * @param appId The ID of the app to retrieve.
     * @return The corresponding `BlockedApp`, or null if not found.
     */
    fun getLimitedAppById(appId: String): BlockedApp? {
        return preferencesManager.getLimitedApps().find { it.id == appId }
    }

    /** Retrieves list of user visible packages - limited to those user can actually use
     * @param context The context of app runtime system
     * @return List of all installed app Info
     * **/
    fun getAllInstalledApps(context: Context): List<ApplicationInfo>  {
        val packageManager = context.packageManager

        // App categories
        val relevantCategories = listOf(
            ApplicationInfo.CATEGORY_AUDIO,
            ApplicationInfo.CATEGORY_VIDEO,
            ApplicationInfo.CATEGORY_GAME,
            ApplicationInfo.CATEGORY_SOCIAL,
            ApplicationInfo.CATEGORY_NEWS,
            ApplicationInfo.CATEGORY_PRODUCTIVITY,
            ApplicationInfo.CATEGORY_MAPS,
            ApplicationInfo.CATEGORY_IMAGE,
            ApplicationInfo.CATEGORY_ACCESSIBILITY,
            ApplicationInfo.CATEGORY_UNDEFINED
        )

        // List of package prefixes to exclude
        val excludedPrefixes = listOf(
            "com.sec.android", // Samsung
            "com.samsung",
            "com.miui", // Xiaomi
            "com.huawei",
            "com.google.android", // Google Services
            "com.android.systemui", // System UI
            "com.android.providers", // OS providers
            "com.qualcomm",
            "com.mediatek"
        )

        // Get all apps
        var installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        // Filter apps for categories and exclude them
        installedApps = installedApps.filter { appInfo ->
            // Check if app can be launched
            val hasLauncherIntent = packageManager.getLaunchIntentForPackage(appInfo.packageName) != null

            // Check if has excluded package prefis
            val isExcludedPackage = excludedPrefixes.any { prefix ->
                appInfo.packageName.startsWith(prefix)
            }

            // Exclude system apps
            val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val isUpdatedSystemApp = (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0

            // Filter for categories
            val isRelevantCategory = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                appInfo.category in relevantCategories
            } else {
                // API lesser than 26 has no support for categories
                false
            }

            // Get only relevant apps
            hasLauncherIntent && !isSystemApp && !isUpdatedSystemApp && !isExcludedPackage && isRelevantCategory
        }

        return installedApps
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
