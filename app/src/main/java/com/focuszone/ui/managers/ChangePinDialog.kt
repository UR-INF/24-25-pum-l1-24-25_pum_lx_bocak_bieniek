package com.focuszone.ui.managers

import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.focuszone.R
import com.focuszone.domain.UserAuthManager

object ChangePinDialog {

    fun show(context: Context, userAuthManager: UserAuthManager) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.fragment_change_pin, null)

        val currentPinEditText = dialogView.findViewById<EditText>(R.id.editTextCurrentPin)
        val newPinEditText = dialogView.findViewById<EditText>(R.id.editTextNewPin)
        val confirmPinEditText = dialogView.findViewById<EditText>(R.id.editTextConfirmNewPin)

        val dialog = AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.change_pin))
            .setView(dialogView)
            .setPositiveButton(context.getString(R.string.save), null) // Listener dodamy poniżej
            .setNegativeButton(context.getString(R.string.cancel), null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val currentPin = currentPinEditText.text.toString()
                val newPin = newPinEditText.text.toString()
                val confirmPin = confirmPinEditText.text.toString()

                if (!userAuthManager.authenticateUser(currentPin)) {
                    Toast.makeText(context, context.getString(R.string.invalid_current_pin), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (newPin.isEmpty() || confirmPin.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.pin_empty), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (newPin != confirmPin) {
                    Toast.makeText(context, context.getString(R.string.pins_do_not_match), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (newPin == currentPin) {
                    Toast.makeText(context, context.getString(R.string.pin_invalid), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }



                userAuthManager.setNewPin(newPin)
                Toast.makeText(context, context.getString(R.string.pin_changed_successfully), Toast.LENGTH_SHORT).show()

                dialog.dismiss()
            }
        }

        dialog.show()
    }
}