package com.meetings.android.model.tools

import android.content.Context
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.meetings.android.BuildConfig
import io.fabric.sdk.android.Fabric
import javax.inject.Inject

class AppTools @Inject constructor(private val context: Context) : Tools {
    private val fabric: Fabric by lazy {
        val kits = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder()
                        .disabled(BuildConfig.DEBUG)
                        .build())
                .build()
        Fabric.Builder(context)
                .kits(kits)
                .debuggable(BuildConfig.DEBUG)
                .build()
    }

    override fun fabric() {
        Fabric.with(fabric)
    }
}