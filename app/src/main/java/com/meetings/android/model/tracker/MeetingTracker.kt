package com.meetings.android.model.tracker

import android.content.Context
import com.meetings.android.entity.Meeting
import com.meetings.android.entity.MeetingStatus
import com.meetings.android.model.firestore.Firestore
import com.meetings.android.utils.calculateRate
import com.meetings.android.utils.launchForegroundService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

private const val TIME_TO_UPDATE = 1000 * 60 * 2 // 2 Minutes

class MeetingTracker @Inject constructor() : Tracker {
    override var meeting: Meeting? = null

    @Inject
    lateinit var firestore: Firestore

    private val meetingObservable = PublishSubject.create<Meeting>()
    private var timeToUpdate: Long = 0

    override fun startTracking(context: Context, meeting: Meeting): PublishSubject<Meeting> {
        meeting.status = MeetingStatus.IN_PROGRESS.status
        this.meeting = meeting
        updateMeeting()
        context.launchForegroundService(TrackerService.getStartTrackingIntent(context))
        return meetingObservable
    }

    override fun stopTracking(context: Context) {
        meeting?.apply {
            status = MeetingStatus.STOPPED.status
            updateMeeting()
            meeting = null
        }
        context.launchForegroundService(TrackerService.getStopTrackingIntent(context))
    }

    override fun updateTrackedTime(time: Long) {
        meeting?.apply {
            meeting!!.trackedTime = meeting!!.trackedTime + time
            meeting!!.cost = meeting!!.cost + meeting!!.calculateRate(time)
            meetingObservable.onNext(meeting!!)
        }
        if (timeToUpdate > TIME_TO_UPDATE) {
            timeToUpdate = 0
            updateMeeting()
        } else {
            timeToUpdate += time
        }
    }

    private fun updateMeeting() {
        meeting?.apply {
            firestore.updateMeeting(this)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            meetingObservable.onNext(this)
        }
    }
}