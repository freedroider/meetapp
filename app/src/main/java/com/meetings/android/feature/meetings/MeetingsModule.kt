package com.meetings.android.feature.meetings

import dagger.Binds
import dagger.Module

@Module
interface MeetingsModule {
    @Binds
    fun provideView(activity: MeetingsActivity): MeetingsActivity
}