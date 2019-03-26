package com.meetings.android.feature.addMeeting

import dagger.Binds
import dagger.Module

@Module
interface AddMeetingModule {
    @Binds
    fun provideView(activity: AddMeetingActivity): AddMeetingActivity
}