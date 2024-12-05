package com.focuszone.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.focuszone.R
import com.focuszone.domain.BlockedSite

class BlockedSitesAdapter(
    private val blockedSites: List<BlockedSite>,
    private val onEditClick: (BlockedSite) -> Unit
) : RecyclerView.Adapter<BlockedSitesAdapter.BlockedSiteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockedSiteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_site_card, parent, false)
        return BlockedSiteViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlockedSiteViewHolder, position: Int) {
        val blockedSite = blockedSites[position]
        holder.bind(blockedSite)
        holder.editButton.setOnClickListener { onEditClick(blockedSite) }
    }

    override fun getItemCount() = blockedSites.size

    inner class BlockedSiteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val siteName: TextView = itemView.findViewById(R.id.siteName)
        private val siteLimit: TextView = itemView.findViewById(R.id.siteLimit)
        val editButton: Button = itemView.findViewById(R.id.editButtonSite)

        fun bind(blockedSite: BlockedSite) {
            siteName.text = blockedSite.name
            siteLimit.text = "Limit: ${blockedSite.limit}"
        }
    }
}

