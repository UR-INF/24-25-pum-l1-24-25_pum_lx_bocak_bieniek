package com.focuszone.domain.services.network

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.focuszone.data.preferences.PreferencesManager
import com.focuszone.util.Constants.KNOWN_BROWSERS

class SiteBlockerService : AccessibilityService() {

    private val preferenceManager = PreferencesManager(this)

    // List of blocked sites (can be dynamically loaded)
    private val blockedSites = preferenceManager.getBlockedSites()

    // Called when an event is intercepted
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // Get the package name of the app where the event occurred
        val packageName = event.packageName?.toString() ?: return

//        val browserInStringRegex = .toRegex()

        if (isBrowserPackage(packageName)) {
            val nodeInfo = event.source ?: return

            // Try to extract the URL
            val url = extractUrlFromNode(nodeInfo)

            if (url != null && isBlocked(url)) {
                // Go back to the previous screen
                performGlobalAction(GLOBAL_ACTION_BACK)
                showToast("The website $url has been blocked by FocusZone.")
            }
        }
    }

    // Called when the service is interrupted
    override fun onInterrupt() {
        // Handle service interruption
    }

    // Function that checks if the app is a browser
    private fun isBrowserPackage(packageName: String): Boolean {
        return KNOWN_BROWSERS.contains(packageName)
    }

    // Function to extract the URL from the node
    private fun extractUrlFromNode(nodeInfo: AccessibilityNodeInfo?): String? {
        if (nodeInfo == null) return null

        // Check if the node contains text that looks like a URL
        if (nodeInfo.text != null && nodeInfo.text.toString().startsWith("http")) {
            return nodeInfo.text.toString()
        }

        // Search the node's children
        for (i in 0 until nodeInfo.childCount) {
            val childNode = nodeInfo.getChild(i)
            val url = extractUrlFromNode(childNode)
            if (url != null) return url
        }

        return null
    }

    // Function that checks if the URL is on the blocked list
    private fun isBlocked(url: String): Boolean {
        return blockedSites.any { blockedSite ->
            url.startsWith(blockedSite.url.trim())
        }
    }

    // Displays a message on the screen
    private fun showToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }
}