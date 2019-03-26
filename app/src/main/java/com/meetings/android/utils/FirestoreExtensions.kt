package com.meetings.android.utils

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.meetings.android.entity.Meeting
import com.meetings.android.entity.Request
import com.meetings.android.entity.User
import io.reactivex.SingleEmitter
import java.lang.Exception
import java.util.*

fun DocumentSnapshot.parse(user: User?): User {
    val id = this.id
    val fullName = getString(user.fieldFullName) as String
    val rate = get(user.fieldRate) as Double
    return User(id, fullName, rate)
}

fun DocumentSnapshot.parse(meeting: Meeting?): Meeting {
    val id = this.id
    val name = getString(meeting.fieldName) as String
    val location = getGeoPoint(meeting.fieldLocation)
    val ownerId = getString(meeting.fieldOwnerId) as String
    val status = get(meeting.fieldStatus) as String
    val type = get(meeting.fieldType) as String
    val trackedTime = getLong(meeting.fieldTrackedTime) as Long
    val cost = getDouble(meeting.fieldCost) as Double
    @Suppress("UNCHECKED_CAST")
    val participantsMap = get(meeting.fieldParticipantsIds)
            as? ArrayList<String>
    val participantIds = ArrayList<String>()
    participantsMap?.forEach {
        participantIds.add(it)
    }
    return Meeting(id = id,
            name = name,
            location = location,
            ownerId = ownerId,
            participantsIds = participantIds,
            status = status,
            type = type,
            trackedTime = trackedTime,
            cost = cost)
}

fun DocumentSnapshot.parse(request: Request?): Request {
    val id = this.id
    val createdAt = getDate(request.fieldCreatedAt) as Date
    val meetingId = getString(request.fieldMeetingId) as String
    val meetingName = getString(request.fieldMeetingName) as String
    val ownerId = getString(request.fieldOwnerId) as String
    val participantId = getString(request.fieldParticipantId) as String
    val participantName = getString(request.fieldParticipantName) as String
    return Request(id = id,
            createdAt = createdAt,
            meetingId = meetingId,
            meetingName = meetingName,
            ownerId = ownerId,
            participantId = participantId,
            participantName = participantName)
}

fun Task<List<DocumentSnapshot>>.parse(meeting: Meeting?, user: User): List<Meeting> {
    val meetings = arrayListOf<Meeting>()
    result.forEach { documentSnapshot ->
        val meetingFirestore = documentSnapshot.parse(meeting)
        if (!meetingFirestore.amIParticipant(user.id)) {
            meetings.add(meetingFirestore)
        }
    }
    return meetings
}

fun QuerySnapshot?.rxFirstOrThrow(user: User, emitter: SingleEmitter<User>) {
    if (this != null && !isEmpty) {
        emitter.onSuccess(documents.first().parse(user))
    } else {
        emitter.onError(Exception("$user doesn't exist in Firestore"))
    }
}

fun QuerySnapshot?.rxFirstOrThrow(request: Request?, emitter: SingleEmitter<Request>) {
    if (this != null && !isEmpty) {
        emitter.onSuccess(documents.first().parse(request))
    } else {
        emitter.onError(Exception("$request doesn't exist in Firestore"))
    }
}