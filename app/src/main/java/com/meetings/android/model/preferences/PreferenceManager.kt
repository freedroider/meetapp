package com.meetings.android.model.preferences

import android.content.Context
import android.content.SharedPreferences
import com.meetings.android.utils.get
import com.meetings.android.utils.save
import javax.inject.Inject

private const val PREFERENCES_NAME = "meetings.preferences"
private const val FIELD_USER_ID = "userId"

class PreferenceManager @Inject constructor(context: Context) : LocalPreferences {
    private val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun saveUserId(userId: String?) {
        sharedPreferences.save(FIELD_USER_ID, userId)
    }

    override fun getUserId(): String? = sharedPreferences.get(FIELD_USER_ID)
}