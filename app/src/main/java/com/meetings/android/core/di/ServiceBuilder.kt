package com.meetings.android.core.di

import com.meetings.android.model.tracker.TrackerService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ServiceBuilder {
    @ContributesAndroidInjector
    fun bindTrackerService(): TrackerService
}
