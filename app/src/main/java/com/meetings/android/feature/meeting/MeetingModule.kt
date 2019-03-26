package com.meetings.android.feature.meeting

import dagger.Binds
import dagger.Module

@Module
interface MeetingModule {
    @Binds
    fun provideView(activity: MeetingActivity): MeetingActivity
}