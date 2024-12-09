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
    private val sites: MutableList<BlockedSite>,
    private val onEditClick: (BlockedSite) -> Unit,
    private val onDeleteClick: (BlockedSite) -> Unit
) : RecyclerView.Adapter<BlockedSitesAdapter.BlockedSiteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockedSiteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_site_card, parent, false)
        return BlockedSiteViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlockedSiteViewHolder, position: Int) {
        val site = sites[position]
        holder.bind(site)

        holder.editButton.setOnClickListener { onEditClick(site) }

        holder.deleteButton.setOnClickListener { onDeleteClick(site) }
    }

    override fun getItemCount(): Int = sites.size

    class BlockedSiteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val siteName: TextView = view.findViewById(R.id.siteName)
        private val siteLimit: TextView = view.findViewById(R.id.siteLimit)
        val editButton: Button = view.findViewById(R.id.editButtonSite)
        val deleteButton: Button = view.findViewById(R.id.deleteButtonApp)

        fun bind(site: BlockedSite) {
            siteName.text = site.name
            siteLimit.text = "Limit: ${site.limit}"
        }
    }
}
