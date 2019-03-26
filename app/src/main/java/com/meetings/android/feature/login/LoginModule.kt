package com.meetings.android.feature.login

import dagger.Binds
import dagger.Module

@Module
interface LoginModule {
    @Binds
    fun provideView(activity: LoginActivity): LoginActivity
}