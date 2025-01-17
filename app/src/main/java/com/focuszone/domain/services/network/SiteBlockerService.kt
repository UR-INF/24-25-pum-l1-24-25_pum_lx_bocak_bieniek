package com.focuszone.domain.services.network

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.focuszone.data.preferences.PreferencesManager
import com.focuszone.util.Constants.KNOWN_BROWSERS

/** Monitor opened Sites in supported browsers
 *
 * HOW TO USE
 *
 * On first app start:
 * val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
 * startActivity(intent)
 *
 * It will start automatically as it is now System Service
 * **/
//
//class SiteBlockerService : AccessibilityService() {
//
//    private var preferenceManager = PreferencesManager(this)
//    private var blockedSites = preferenceManager.getBlockedSites()
//
//    /** On event - opened browser package - check URL in Preferences
//     * block if url is in Preferences
//     * "block" means GLOBAL_ACTION_BACK
//     * TODO
//     * **/
//    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
//        if (event == null) return
//
//        val packageName = event.packageName?.toString() ?: return
//
//        if (isBrowserPackage(packageName)) {
//            val nodeInfo = event.source ?: return
//
//            val url = extractUrlFromNode(nodeInfo)
//
//            if (url != null && isBlocked(url)) {
//                performGlobalAction(GLOBAL_ACTION_BACK)
//                Toast.makeText(applicationContext, "The website $url has been blocked by FocusZone.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    // Called when the service is interrupted
//    override fun onInterrupt() {
//        // Handle service interruption
//    }
//
//    // Checks if the app package is in KNOWS_BROWSERS
//    private fun isBrowserPackage(packageName: String): Boolean {
//        return KNOWN_BROWSERS.contains(packageName)
//    }
//
//    // Extract the URL from the node information
//    private fun extractUrlFromNode(nodeInfo: AccessibilityNodeInfo?): String? {
//        if (nodeInfo == null) return null
//
//        // Check if the node contains text that looks like a URL
//        if (nodeInfo.text != null && nodeInfo.text.toString().startsWith("http")) {
//            return nodeInfo.text.toString()
//        }
//
//        // Search the node's children
//        for (i in 0 until nodeInfo.childCount) {
//            val childNode = nodeInfo.getChild(i)
//            val url = extractUrlFromNode(childNode)
//            if (url != null) return url
//        }
//
//        return null
//    }
//
//    // Check if the URL is on the blocked list
//    private fun isBlocked(url: String): Boolean {
//        return blockedSites.any { blockedSite ->
//            url.startsWith(blockedSite.url.trim())
//        }
//    }
//
//    // Displays a message on the screen
//    private fun showToast(message: String) {
//        Handler(Looper.getMainLooper()).post {
//        }
//    }
//}