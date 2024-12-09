package com.focuszone.ui.fragments

import android.app.AlertDialog
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
import com.focuszone.domain.BlockedSite
import com.focuszone.ui.adapters.BlockedSitesAdapter

class BlockedSitesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_blocked_sites, container, false)

        recyclerView = view.findViewById(R.id.blockedSitesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Sample data to view in app
        // TODO: add blocked sites from device
        val blockedSites = mutableListOf(
            BlockedSite("example.com", "1h"),
            BlockedSite("another-site.com", "2h"),
            BlockedSite("blocked-site.com", "30m")
        )

        val navController = findNavController()

        recyclerView.adapter = BlockedSitesAdapter(
            sites = blockedSites,
            onEditClick = { site ->
                val bundle = Bundle().apply {
                    putString("siteName", site.name)
                }
                navController.navigate(R.id.editSiteFragment, bundle)
            },
            onDeleteClick = { site ->
                showDeleteConfirmationDialog(site, blockedSites)
            }
        )

        return view
    }

    private fun showDeleteConfirmationDialog(site: BlockedSite, sites: MutableList<BlockedSite>) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmation of deletion")
            .setMessage("Are you sure you want to remove the site restriction ${site.name}?")
            .setPositiveButton("Yes") { _, _ ->
//                sites.remove(site)
//                recyclerView.adapter?.notifyDataSetChanged()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addButton: Button = view.findViewById(R.id.bttnAddSite)

        addButton.setOnClickListener {
            findNavController().navigate(R.id.addSiteFragment)
        }
    }
}