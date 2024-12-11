package com.focuszone.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.focuszone.R
import com.focuszone.data.preferences.entities.BlockedSiteEntity
import com.focuszone.ui.adapters.BlockedSitesAdapter

class BlockedSitesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var blockedSites: MutableList<BlockedSiteEntity>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_blocked_sites, container, false)

        recyclerView = view.findViewById(R.id.blockedSitesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Sample data to view in app
        // TODO: add blocked sites from device
        blockedSites = mutableListOf(
            BlockedSiteEntity("example.com"),
            BlockedSiteEntity("another-site.com"),
            BlockedSiteEntity("blocked-site.com")
        )

        recyclerView.adapter = BlockedSitesAdapter(
            sites = blockedSites,
            onDeleteClick = { site ->
                showDeleteConfirmationDialog(site, blockedSites)
            }
        )

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addButton: Button = view.findViewById(R.id.bttnAddSite)

        addButton.setOnClickListener {
            showAddSiteDialog()
        }
    }

    private fun showAddSiteDialog() {
        val dialogView = layoutInflater.inflate(R.layout.fragment_add_site, null)
        val editTextSiteAddress = dialogView.findViewById<EditText>(R.id.editTextSiteAddress)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Blocked Site")
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<Button>(R.id.buttonAdd).setOnClickListener {
            val siteAddress = editTextSiteAddress.text.toString()

            if (siteAddress.isNotBlank()) {
                blockedSites.add(BlockedSiteEntity(siteAddress))
                recyclerView.adapter?.notifyItemInserted(blockedSites.size - 1)

                Toast.makeText(requireContext(), "Added: $siteAddress", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                editTextSiteAddress.error = "Address cannot be empty"
            }
        }

        dialogView.findViewById<Button>(R.id.buttonCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(site: BlockedSiteEntity, sites: MutableList<BlockedSiteEntity>) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmation of deletion")
            .setMessage("Are you sure you want to remove the site restriction ${site.url}?")
            .setPositiveButton("Yes") { _, _ ->
//                  delete site from list and refresh RecyclerView
//                val position = sites.indexOf(site)
//                if (position != -1) {
//                    sites.removeAt(position)
//                    recyclerView.adapter?.notifyItemRemoved(position)
//                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
