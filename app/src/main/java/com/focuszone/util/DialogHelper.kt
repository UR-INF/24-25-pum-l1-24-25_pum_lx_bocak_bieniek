package com.focuszone.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Settings
import android.util.Log
import android.view.WindowManager

class DialogHelper {
    companion object {
        private var isDialogShown = false
        private var shouldContinue = false

        // Funkcja sprawdzająca czy package jest launcherem
        private fun isHomeLauncher(context: Context, packageName: String): Boolean {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
            }
            val resolveInfo = context.packageManager.resolveActivity(intent, 0)
            return resolveInfo?.activityInfo?.packageName == packageName
        }

        fun showBlockingAlert(context: Context, message: String) {
            if (!Settings.canDrawOverlays(context)) {
                Log.d("DialogHelper", "Cannot draw overlays")
                return
            }

            if (isDialogShown && shouldContinue) {
                Log.d("DialogHelper", "Dialog already shown and user chose to continue")
                return
            }

            Log.d("DialogHelper", "Showing blocking alert with message: $message")

            val builder = AlertDialog.Builder(context)
                .setTitle("Uwaga")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Kontynuuj") { dialog, _ ->
                    dialog.dismiss()
                    isDialogShown = true
                    shouldContinue = true
                    Log.d("DialogHelper", "User chose to continue")
                }
                .setNegativeButton("Zakończ") { dialog, _ ->
                    dialog.dismiss()
                    isDialogShown = true
                    shouldContinue = false
                    Log.d("DialogHelper", "User chose to exit")
                    val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_HOME)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(homeIntent)
                    if (context is android.app.Activity) {
                        context.finish()
                    }
                }

            val dialog = builder.create().apply {
                window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
                window?.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            }

            dialog.show()
        }

        // Nowa metoda do sprawdzania i resetowania stanu
        fun checkAndResetState(context: Context, packageName: String) {
            if (isHomeLauncher(context, packageName)) {
                Log.d("DialogHelper", "Home launcher detected, resetting dialog state")
                reset()
            }
        }

        private fun reset() {
            isDialogShown = false
            shouldContinue = false
            Log.d("DialogHelper", "Dialog state reset")
        }
    }
}