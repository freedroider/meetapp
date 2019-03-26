package com.meetings.android.model.tracker

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.meetings.android.R
import com.meetings.android.core.base.BaseService
import com.meetings.android.entity.Meeting
import com.meetings.android.feature.meeting.MeetingActivity
import com.meetings.android.utils.createChannel
import com.meetings.android.utils.createNotification
import com.meetings.android.utils.notificationManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val ACTION_START_TRACKING = "action.startTracking"
private const val ACTION_STOP_TRACKING = "action.stopTracking"
private const val CHANNEL_ID = "com.meetings.android.model.tracker.CHANNEL_ID"
private const val CHANNEL_NAME = "Meeting Channel"
private const val NOTIFICATION_ID = 310
private const val NOTIFICATION_REQUEST_CODE = 603

class TrackerService : BaseService() {
    @Inject
    lateinit var tracker: Tracker
    lateinit var trackingTimer: TrackingTimer

    override fun onCreate() {
        super.onCreate()
        trackingTimer = TrackingTimer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TRACKING -> startTracking()
            ACTION_STOP_TRACKING -> stopTracking()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTracking() {
        startForeground()
        trackingTimer.start {
            tracker.updateTrackedTime(it)
            updateForegroundNotification()
        }
    }

    private fun stopTracking() {
        trackingTimer.stop()
        startForeground()
        stopForeground(true)
        stopSelf()
    }

    private fun startForeground() {
        createChannel(CHANNEL_ID, CHANNEL_NAME)
        val notification = createNotification(tracker.meeting)
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun updateForegroundNotification() {
        val notification = createNotification(tracker.meeting)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotification(meeting: Meeting?): Notification {
        val message = if (meeting != null) {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(meeting.trackedTime).toInt()
            resources.getQuantityString(R.plurals.minutes, minutes, minutes)
        } else {
            null
        }
        val pendingIntent: PendingIntent? = if (meeting != null) {
            PendingIntent.getActivity(this,
                    NOTIFICATION_REQUEST_CODE,
                    MeetingActivity.getMeetingIntent(this, meeting).apply {
                    },
                    PendingIntent.FLAG_ONE_SHOT)
        } else {
            null
        }
        return createNotification(channelId = CHANNEL_ID,
                icon = R.drawable.ic_notification,
                title = meeting?.name ?: getString(R.string.app_name),
                pendingIntent = pendingIntent,
                meesage = message)
    }

    companion object {
        fun getStartTrackingIntent(context: Context): Intent =
                Intent(context, TrackerService::class.java)
                        .apply {
                            action = ACTION_START_TRACKING
                        }

        fun getStopTrackingIntent(context: Context): Intent =
                Intent(context, TrackerService::class.java)
                        .apply {
                            action = ACTION_STOP_TRACKING
                        }
    }
}