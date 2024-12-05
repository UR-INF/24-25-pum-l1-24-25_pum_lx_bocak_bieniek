package com.focuszone.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.focuszone.R
import com.focuszone.domain.BlockedApp
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
        val blockedApps = listOf(
            BlockedApp("Facebook", R.drawable.baseline_block, "1h 30min"),
            BlockedApp("Instagram", R.drawable.baseline_block, "1h 30min"),
            BlockedApp("Twitter", R.drawable.baseline_block, "1h 30min"),
            BlockedApp("TikTok", R.drawable.baseline_block, "1h 30min")
        )

        val navController = findNavController()

        recyclerView.adapter = BlockedAppsAdapter(blockedApps) { app ->
            val bundle = Bundle().apply {
                putString("appName", app.name)
            }
            navController.navigate(R.id.editAppFragment, bundle)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addButton: Button = view.findViewById(R.id.bttnAddApp)

        addButton.setOnClickListener {
            findNavController().navigate(R.id.addAppFragment)
        }
    }
}
