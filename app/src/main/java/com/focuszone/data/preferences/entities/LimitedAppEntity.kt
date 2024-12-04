package com.focuszone.data.preferences.entities

//Represents app with set time limit
data class LimitedAppEntity (
    val id: String,
    val isLimitSet: Boolean,
    val isSessionsSet: Boolean,
    val limitMinutes: Int?,
    val numberOfSessions: Int?,
    val sessionMinutes: Int?
)