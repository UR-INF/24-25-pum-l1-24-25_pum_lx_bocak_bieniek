package com.focuszone.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.focuszone.R
import com.focuszone.data.preferences.entities.BlockedApp
import com.focuszone.ui.adapters.BlockedAppsAdapter
import com.focuszone.domain.AppManager

class BlockedAppsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var blockedAppsAdapter: BlockedAppsAdapter

    private val blockedApps = mutableListOf<BlockedApp>()
    private val filteredApps = mutableListOf<BlockedApp>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_blocked_apps, container, false)

        recyclerView = view.findViewById(R.id.restrictedAppsRecyclerView)
        searchView = view.findViewById(R.id.searchView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        blockedAppsAdapter = BlockedAppsAdapter(
            apps = filteredApps,
            onEditClick = { app ->
                val bundle = Bundle().apply {
                    putString("appName", app.id)
                }
                findNavController().navigate(R.id.editAppFragment, bundle)
            },
            onLimitToggle = { app, isEnabled ->
                val appManager = AppManager(requireContext())
                try {
                    if (isEnabled) {
                        appManager.enableLimitForApp(app.id)
                        Log.d("BlockedAppsFragment", "Enabled limit for app: ${app.appName} (${app.id})")
                    } else {
                        appManager.disableLimitForApp(app.id)
                        Log.d("BlockedAppsFragment", "Disabled limit for app: ${app.appName} (${app.id})")
                    }
                } catch (e: Exception) {
                    blockedAppsAdapter.notifyDataSetChanged()
                    Log.e("BlockedAppsFragment", "Error toggling limit for ${app.appName} (${app.id})", e)
                }
            }
        )


        recyclerView.adapter = blockedAppsAdapter

        setupSearchView()
        loadUserApps()

        return view
    }

    private fun loadUserApps() {
        val appManager = AppManager(requireContext())
        val installedApps = appManager.getAllInstalledApps(requireContext())
        val packageManager = requireContext().packageManager

        val limitedApps = appManager.getAllLimitedApps()

        blockedApps.clear()
        blockedApps.addAll(installedApps.map { appInfo ->
            val appId = appInfo.packageName
            val appName = appInfo.loadLabel(packageManager).toString()

            val savedLimitApp = limitedApps.find { it.id == appId }

            BlockedApp(
                id = appId,
                appName = appName,
                isLimitSet = savedLimitApp?.isLimitSet ?: false,
                limitMinutes = savedLimitApp?.limitMinutes ?: 0,
                currentTimeUsage = savedLimitApp?.currentTimeUsage ?: 0,
                icon = appInfo.loadIcon(packageManager)
            )
        })

        filteredApps.clear()
        filteredApps.addAll(blockedApps)
        blockedAppsAdapter.notifyDataSetChanged()
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterApps(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterApps(newText)
                return true
            }
        })
    }

    private fun filterApps(query: String?) {
        val searchText = query?.lowercase()?.trim() ?: ""

        filteredApps.clear()
        if (searchText.isEmpty()) {
            filteredApps.addAll(blockedApps)
        } else {
            filteredApps.addAll(
                blockedApps.filter { it.id.lowercase().contains(searchText) }
            )
        }
        blockedAppsAdapter.notifyDataSetChanged()
    }
}
