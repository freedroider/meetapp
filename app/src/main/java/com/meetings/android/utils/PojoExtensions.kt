package com.meetings.android.utils

import com.meetings.android.entity.Meeting
import com.meetings.android.entity.MeetingStatus
import com.meetings.android.entity.MeetingType
import com.meetings.android.entity.Request
import java.text.SimpleDateFormat
import java.util.*

val dateFormat = SimpleDateFormat("MMM d, EEE", Locale.ENGLISH)

fun Meeting.calculateRate(time: Long): Double {
    val meetingRate = participants.sumByDouble {
        it.rate
    }
    return time.msToHours() * meetingRate
}

fun Meeting.amIOwner(userId: String?) = ownerId.equals(userId)

fun Meeting.amIParticipant(userId: String?) = participantsIds.contains(userId)

val Meeting.isCreated: Boolean
    get() = status == MeetingStatus.CREATED.status

val Meeting.isInProgress: Boolean
    get() = status == MeetingStatus.IN_PROGRESS.status

val Meeting.isStopped: Boolean
    get() = status == MeetingStatus.STOPPED.status

val Meeting.isPublic: Boolean
    get() = type == MeetingType.PUBLIC.type

val Meeting.isPrivate: Boolean
    get() = type == MeetingType.PRIVATE.type

val Meeting.isHidden: Boolean
    get() = type == MeetingType.HIDDEN.type

fun Request.formatCreatedAt() = dateFormat.format(createdAt)