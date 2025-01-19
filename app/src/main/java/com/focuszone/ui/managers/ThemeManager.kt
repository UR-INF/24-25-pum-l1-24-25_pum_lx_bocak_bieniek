package com.focuszone.utils

import android.content.Context
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import com.focuszone.R

object ThemeManager {
    fun setupDarkModeSwitch(context: Context, switchDarkMode: Switch) {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false)

        switchDarkMode.isChecked = isDarkModeEnabled
        switchDarkMode.text = if (isDarkModeEnabled) context.getString(R.string.disable) else context.getString(R.string.enable)

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            switchDarkMode.text = if (isChecked) context.getString(R.string.disable) else context.getString(R.string.enable)

            sharedPreferences.edit()
                .putBoolean("dark_mode", isChecked)
                .apply()
        }
    }
}
