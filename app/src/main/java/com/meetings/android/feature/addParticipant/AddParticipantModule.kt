package com.meetings.android.feature.addParticipant

import dagger.Binds
import dagger.Module

@Module
interface AddParticipantModule {
    @Binds
    fun provideView(activity: AddParticipantActivity): AddParticipantActivity
}