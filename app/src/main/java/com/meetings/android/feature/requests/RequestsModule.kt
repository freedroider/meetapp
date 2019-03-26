package com.meetings.android.feature.requests

import dagger.Binds
import dagger.Module

@Module
interface RequestsModule {
    @Binds
    fun provideView(activity: RequestsActivity): RequestsActivity
}