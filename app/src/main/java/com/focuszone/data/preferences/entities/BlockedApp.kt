package com.focuszone.data.preferences.entities

import android.graphics.drawable.Drawable

//Represents app with set time limit
data class BlockedApp(
    val id: String,
    var isLimitSet: Boolean,
    val limitMinutes: Int?,
    val currentTimeUsage: Int?,
    val icon: Drawable?
)
