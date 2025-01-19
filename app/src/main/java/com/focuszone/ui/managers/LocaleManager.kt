package com.focuszone.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleManager {
    fun setLocale(activity: Activity, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        activity.resources.updateConfiguration(config, activity.resources.displayMetrics)

        activity.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            .edit()
            .putString("app_language", languageCode)
            .apply()

        activity.recreate()
    }
}
