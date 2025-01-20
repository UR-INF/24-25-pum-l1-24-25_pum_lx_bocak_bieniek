package com.focuszone.ui.dialogs

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.focuszone.R
import com.focuszone.data.preferences.PreferencesManager

object CustomMessageDialog {
    fun show(context: Context, preferencesManager: PreferencesManager) {
        val dialogView = View.inflate(context, R.layout.fragment_custom_message, null)
        val messageEditText = dialogView.findViewById<EditText>(R.id.editTextCustomMessage)

        messageEditText.setText(preferencesManager.getCustomMessage())

        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.change_custom_message))
            .setView(dialogView)
            .setPositiveButton(context.getString(R.string.save)) { _, _ ->
                val newMessage = messageEditText.text.toString()
                if (newMessage.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.message_empty), Toast.LENGTH_SHORT).show()
                } else {
                    preferencesManager.saveUserMessage(newMessage)
                    Toast.makeText(context, context.getString(R.string.message_saved), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(context.getString(R.string.cancel), null)
            .show()
    }
}
