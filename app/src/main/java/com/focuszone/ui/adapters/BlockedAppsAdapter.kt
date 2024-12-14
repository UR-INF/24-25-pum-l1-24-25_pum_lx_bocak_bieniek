package com.focuszone.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.focuszone.R
import com.focuszone.data.preferences.entities.BlockedApp

class BlockedAppsAdapter(
    private val apps: MutableList<BlockedApp>,
    private val onEditClick: (BlockedApp) -> Unit
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
        holder.appSwitch.isChecked = app.isLimitSet
        holder.appSwitch.text = if (app.isLimitSet) {
            holder.itemView.context.getString(R.string.disable)
        } else {
            holder.itemView.context.getString(R.string.enable)
        }

        holder.appSwitch.setOnCheckedChangeListener { _, isChecked ->
            app.isLimitSet = isChecked
            holder.appSwitch.text = if (isChecked) {
                holder.itemView.context.getString(R.string.disable)
            } else {
                holder.itemView.context.getString(R.string.enable)
            }
        }
    }

    override fun getItemCount(): Int = apps.size

    class BlockedAppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val appName: TextView = view.findViewById(R.id.appName)
        private val appLimit: TextView = view.findViewById(R.id.appLimit)
        val editButton: Button = view.findViewById(R.id.editButtonApp)
        val appSwitch: Switch = view.findViewById(R.id.switchEnableBlock)


        fun bind(app: BlockedApp) {
            appName.text = app.id
            appLimit.text = "Limit: ${app.limitMinutes}"
        }
    }
}