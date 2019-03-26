package com.meetings.android.model.tracker

import java.util.*

private const val TIMER_PERIOD = 2_000L

class TrackingTimer {
    private var timer: Timer? = null

    fun start(callback: (Long) -> Unit) {
        stop()
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                callback.invoke(TIMER_PERIOD)
            }
        }, 0, TIMER_PERIOD)
    }

    fun stop() {
        timer?.cancel()
        timer = null
    }

    fun isStarted() = timer != null
}