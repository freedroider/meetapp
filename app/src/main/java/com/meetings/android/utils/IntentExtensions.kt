package com.meetings.android.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat

inline fun <reified T : Any> Activity.launchActivityForResult(requestCode: Int = -1,
                                                              noinline init: Intent.() ->
                                                              Unit = {}) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivityForResult(intent, requestCode)
}

inline fun <reified T : Any> Activity.launchActivityForResult(requestCode: Int = -1,
                                                              intent: Intent) {
    startActivityForResult(intent, requestCode)
}

inline fun <reified T : Any> Activity.launchActivity(noinline init: Intent.() -> Unit = {}) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent)
}

fun Activity.launchActivity(intent: Intent) {
    startActivity(intent)
}

fun Fragment.launchActivity(intent: Intent) {
    startActivity(intent)
}

inline fun <reified T : Any> Activity.launchActivity(noinline init: Intent.() -> Unit = {},
                                                     bundle: Bundle) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent, bundle)
}

inline fun <reified T : Any> Activity.launchActivity(bundle: Bundle) {
    val intent = newIntent<T>(this)
    startActivity(intent, bundle)
}

inline fun <reified T : Any> Context.launchActivity(noinline init: Intent.() -> Unit = {}) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent)
}

inline fun <reified T : Any> Context.launchService(
        noinline init: Intent.() -> Unit = {}) {
    val intent = newIntent<T>(this)
    intent.init()
    startService(intent)
}

inline fun <reified T : Any> Context.launchForegroundService(
        noinline init: Intent.() -> Unit = {}) {
    val intent = newIntent<T>(this)
    intent.init()
    ContextCompat.startForegroundService(this, intent)
}

fun Context.launchForegroundService(intent: Intent) {
    ContextCompat.startForegroundService(this, intent)
}

inline fun <reified T : Any> newIntent(context: Context): Intent = Intent(context, T::class.java)
