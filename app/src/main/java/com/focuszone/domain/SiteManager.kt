package com.focuszone.domain

import android.content.Context
import com.focuszone.data.preferences.PreferencesManager
import com.focuszone.data.preferences.entities.BlockedSiteEntity

class SiteManager(context: Context) {

    private val preferencesManager = PreferencesManager(context)

    /**
     * Adds or updates a site in the list of blocked sites.
     * @param site The site to add or update.
     * @return true if the operation was successful.
     */
    fun addOrUpdateBlockedSite(site: BlockedSiteEntity): Boolean {
        return try {
            preferencesManager.addOrUpdateBlockedSite(site)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Removes a site from the list of blocked sites.
     * @param url The URL of the site to remove.
     * @return true if the operation was successful.
     */
    fun removeBlockedSite(url: String): Boolean {
        return try {
            preferencesManager.removeBlockedSite(url)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Retrieves a list of all blocked sites.
     * @return A list of `BlockedSiteEntity` objects.
     */
    fun getAllBlockedSites(): List<BlockedSiteEntity> {
        return preferencesManager.getBlockedSites()
    }

    /**
     * Retrieves site url of given blocked sites.
     * @return A url String.
     */
    fun getSiteUrl(site: BlockedSiteEntity): String {
        return site.url
    }
}
