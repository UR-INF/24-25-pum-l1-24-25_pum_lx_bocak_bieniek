package com.focuszone.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.focuszone.R
import com.focuszone.domain.BlockedApp

class BlockedAppsAdapter(
    private val apps: List<BlockedApp>,
    private val onEditClick: (BlockedApp) -> Unit,
    private val onDeleteClick: (BlockedApp) -> Unit
) : RecyclerView.Adapter<BlockedAppsAdapter.BlockedAppViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockedAppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app_card, parent, false)
        return BlockedAppViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlockedAppViewHolder, position: Int) {
        val app = apps[position]
        holder.bind(app)
        holder.editButton.setOnClickListener { onEditClick(app) }
        holder.deleteButton.setOnClickListener { onDeleteClick(app) }
    }

    override fun getItemCount(): Int = apps.size

    class BlockedAppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val appName: TextView = view.findViewById(R.id.appName)
        private val appLimit: TextView = view.findViewById(R.id.appLimit)
        val editButton: Button = view.findViewById(R.id.editButtonApp)
        val deleteButton: Button = view.findViewById(R.id.deleteButtonApp)

        fun bind(app: BlockedApp) {
            appName.text = app.name
            appLimit.text = "Limit: ${app.limit}"
        }
    }
}