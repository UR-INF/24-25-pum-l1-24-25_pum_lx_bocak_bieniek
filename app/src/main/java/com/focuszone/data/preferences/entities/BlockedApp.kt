package com.focuszone.data.preferences.entities

//Represents app with set time limit
data class BlockedApp(
    val id: String,
    var isLimitSet: Boolean,
    val isSessionsSet: Boolean,
    val limitMinutes: Int?,
    val numberOfSessions: Int?,
    val sessionMinutes: Int?,
    val currentTimeUsage: Int?,
    val currentSessionUsage: Int?
)
