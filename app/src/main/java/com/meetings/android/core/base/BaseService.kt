package com.meetings.android.core.base

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.android.AndroidInjection

abstract class BaseService : Service() {
    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        AndroidInjection.inject(this)
    }
}