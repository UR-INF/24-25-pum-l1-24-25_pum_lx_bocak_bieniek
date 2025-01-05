package com.focuszone.util

import android.app.AlertDialog
import android.content.Context
import android.view.WindowManager

class DialogHelper {
    companion object {
        fun showBlockingAlert(context: Context, message: String) {
            val builder = AlertDialog.Builder(context)
                .setTitle("App Blocked")
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }

            val dialog = builder.create()
            dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            dialog.show()
        }
    }
}