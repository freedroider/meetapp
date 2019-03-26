package com.meetings.android.core.di

import android.app.Application
import android.content.Context
import com.meetings.android.model.firestore.Firestore
import com.meetings.android.model.firestore.FirestoreManager
import com.meetings.android.model.location.LocationSource
import com.meetings.android.model.location.MeetingLocationManager
import com.meetings.android.model.preferences.LocalPreferences
import com.meetings.android.model.preferences.PreferenceManager
import com.meetings.android.model.tools.AppTools
import com.meetings.android.model.tools.Tools
import com.meetings.android.model.tracker.MeetingTracker
import com.meetings.android.model.tracker.Tracker
import dagger.Binds
import dagger.Module
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Module(includes = [AndroidSupportInjectionModule::class])
interface AppModule {
    @Singleton
    @Binds
    fun provideContext(app: Application): Context

    @Singleton
    @Binds
    fun provideTools(tools: AppTools): Tools

    @Binds
    @Singleton
    fun provideFirebaseSource(firebaseManager: FirestoreManager): Firestore

    @Binds
    @Singleton
    fun provideTracker(tracker: MeetingTracker): Tracker

    @Singleton
    @Binds
    fun providePreference(prefManager: PreferenceManager): LocalPreferences

    @Singleton
    @Binds
    fun provideLocation(location: MeetingLocationManager): LocationSource
}
