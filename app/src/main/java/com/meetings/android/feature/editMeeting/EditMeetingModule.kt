package com.meetings.android.feature.editMeeting

import dagger.Binds
import dagger.Module

@Module
interface EditMeetingModule {
    @Binds
    fun provideView(activity: EditMeetingActivity): EditMeetingActivity
}