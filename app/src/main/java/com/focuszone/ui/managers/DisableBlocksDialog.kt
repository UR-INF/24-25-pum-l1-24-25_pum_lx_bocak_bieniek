package com.focuszone.ui.managers

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.focuszone.R
import com.focuszone.data.preferences.PreferencesManager

class DisableBlocksDialog(
    private val context: Context,
    private val preferencesManager: PreferencesManager
) {
    fun show() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.fragment_disable_blocks, null)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<Button>(R.id.bttnConfirmDisable).setOnClickListener {
            preferencesManager.removeAllAppLimits()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.bttnCancelDisable).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}