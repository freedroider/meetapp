package com.meetings.android.model.preferences

interface LocalPreferences {
    fun saveUserId(userId: String?)

    fun getUserId() : String?
}