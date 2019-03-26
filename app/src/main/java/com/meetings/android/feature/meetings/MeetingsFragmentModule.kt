package com.meetings.android.feature.meetings

import dagger.Binds
import dagger.Module

@Module
interface MeetingsFragmentModule {
    @Binds
    fun provideView(fragment: MeetingsFragment): MeetingsFragment
}