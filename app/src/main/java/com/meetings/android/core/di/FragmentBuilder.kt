package com.meetings.android.core.di

import com.meetings.android.feature.meetings.MeetingsFragment
import com.meetings.android.feature.meetings.MeetingsFragmentModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface FragmentBuilder {
    @ContributesAndroidInjector(modules = [(MeetingsFragmentModule::class)])
    fun bindCreatedMeetingFragmentInjector(): MeetingsFragment
}