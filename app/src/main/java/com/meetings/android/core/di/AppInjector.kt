package com.meetings.android.core.di

import com.meetings.android.core.App

object AppInjector {
    fun initDagger(app: App) {
        DaggerAppComponent.builder()
                .application(app)
                .build()
                .inject(app)
    }
}
