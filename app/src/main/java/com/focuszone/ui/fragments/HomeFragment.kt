package com.focuszone.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import com.focuszone.R

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val blockedApps = listOf("Facebook", "Instagram", "Twitter", "TikTok")
    private val blockedWebsites = listOf("example.com", "another-site.com", "blocked-site.com")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val blockedAppsCount: TextView = view.findViewById(R.id.blockedAppsCount)
        val blockedWebsitesCount: TextView = view.findViewById(R.id.blockedWebsitesCount)

        blockedAppsCount.text = "Blocked Apps: ${blockedApps.size}"
        blockedWebsitesCount.text = "Blocked Websites: ${blockedWebsites.size}"
    }
}