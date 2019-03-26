package com.meetings.android.core

import android.app.Activity
import android.app.Application
import android.app.Service
import android.support.v4.app.Fragment
import com.meetings.android.core.di.AppInjector
import com.meetings.android.model.tools.Tools
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class App : Application(), HasActivityInjector, HasSupportFragmentInjector, HasServiceInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
    @Inject
    lateinit var dispatchingFragmentAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>

    @Inject
    lateinit var tools: Tools

    override fun onCreate() {
        super.onCreate()
        AppInjector.initDagger(this)
        tools.fabric()
    }

    override fun activityInjector(): AndroidInjector<Activity>? = dispatchingAndroidInjector

    override fun supportFragmentInjector() = dispatchingFragmentAndroidInjector

    override fun serviceInjector(): AndroidInjector<Service> = dispatchingServiceInjector
}