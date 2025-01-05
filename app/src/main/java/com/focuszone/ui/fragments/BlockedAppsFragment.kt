package com.focuszone.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.focuszone.R
import com.focuszone.data.preferences.entities.BlockedApp
import com.focuszone.ui.adapters.BlockedAppsAdapter

class BlockedAppsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_blocked_apps, container, false)

        recyclerView = view.findViewById(R.id.restrictedAppsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Sample data to view in app
        // TODO: add blocked apps from device
        val blockedApps = mutableListOf(
            BlockedApp( "Facebook",  true,  90, null),
            BlockedApp( "Instagram",  true, 90, null),
            BlockedApp( "Twitter",  true, 90, null),
            BlockedApp( "TikTok",  true, 90, null)
        )

        val navController = findNavController()

        recyclerView.adapter = BlockedAppsAdapter(
            apps = blockedApps,
            onEditClick = { app ->
                val bundle = Bundle().apply {
                    putString("appName", app.id)
                }
                navController.navigate(R.id.editAppFragment, bundle)
            }
        )

        return view
    }
}
