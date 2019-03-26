package com.meetings.android.model.tracker

import android.content.Context
import com.meetings.android.entity.Meeting
import io.reactivex.subjects.PublishSubject

interface Tracker {
    var meeting: Meeting?

    fun startTracking(context: Context, meeting: Meeting): PublishSubject<Meeting>

    fun stopTracking(context: Context)

    fun updateTrackedTime(time: Long)
}