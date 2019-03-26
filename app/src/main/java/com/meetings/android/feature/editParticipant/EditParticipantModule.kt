package com.meetings.android.feature.editParticipant

import dagger.Binds
import dagger.Module

@Module
interface EditParticipantModule {
    @Binds
    fun provideView(activity: EditParticipantActivity): EditParticipantActivity
}