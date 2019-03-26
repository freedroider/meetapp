package com.meetings.android.utils

import android.app.NotificationManager
import android.content.Context
import android.view.inputmethod.InputMethodManager

val Context.notificationManager
    get() = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

val Context.inputMethodManager
    get() = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager