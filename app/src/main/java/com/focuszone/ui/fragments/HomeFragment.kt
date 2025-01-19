package com.focuszone.ui.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.focuszone.R
import com.focuszone.data.preferences.PreferencesManager
import com.focuszone.data.preferences.entities.BlockedApp

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var blockedAppsAdapter: BlockedAppsAdapter
    private lateinit var preferencesManager: PreferencesManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferencesManager = PreferencesManager(requireContext())

        recyclerView = view.findViewById(R.id.blockedAppsList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        blockedAppsAdapter = BlockedAppsAdapter()
        recyclerView.adapter = blockedAppsAdapter

        loadBlockedApps()
    }

    private fun loadBlockedApps() {
        val blockedApps = preferencesManager.getLimitedApps().map { app ->
            app.copy(icon = fetchAppIcon(requireContext(), app.id))
        }

        blockedAppsAdapter.updateApps(blockedApps)
    }
    private fun fetchAppIcon(context: Context, packageName: String): Drawable? {
        return try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            context.getDrawable(R.drawable.logo_focus_zone)
        }
    }
}

class BlockedAppsAdapter : RecyclerView.Adapter<BlockedAppsAdapter.ViewHolder>() {
    private var blockedApps = listOf<BlockedApp>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appIcon: ImageView = view.findViewById(R.id.appIcon)
        val appName: TextView = view.findViewById(R.id.appName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.blocked_app_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = blockedApps[position]
        holder.appIcon.setImageDrawable(app.icon)
        holder.appName.text = app.appName
    }

    override fun getItemCount() = blockedApps.size

    fun updateApps(apps: List<BlockedApp>) {
        blockedApps = apps
        notifyDataSetChanged()
    }
}