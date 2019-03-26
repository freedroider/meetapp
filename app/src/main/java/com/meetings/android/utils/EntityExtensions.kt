package com.meetings.android.utils

import com.meetings.android.entity.Meeting
import com.meetings.android.entity.Request
import com.meetings.android.entity.User

val User?.collectionName: String
    get() = "users"

val User?.fieldFullName: String
    get() = "fullName"

val User?.fieldRate: String
    get() = "rate"

val Meeting?.collectionName: String
    get() = "meetings"

val Meeting?.fieldName: String
    get() = "name"

val Meeting?.fieldLocation: String
    get() = "location"

val Meeting?.fieldOwnerId: String
    get() = "ownerId"

val Meeting?.fieldParticipantsIds: String
    get() = "participantsIds"

val Meeting?.fieldStatus: String
    get() = "status"

val Meeting?.fieldCost: String
    get() = "cost"

val Meeting?.fieldTrackedTime: String
    get() = "trackedTime"

val Meeting?.fieldType: String
    get() = "type"

val Request?.collectionName: String
    get() = "requests"

val Request?.fieldCreatedAt: String
    get() = "createdAt"

val Request?.fieldMeetingId: String
    get() = "meetingId"

val Request?.fieldMeetingName: String
    get() = "meetingName"

val Request?.fieldOwnerId: String
    get() = "ownerId"

val Request?.fieldParticipantId: String
    get() = "participantId"

val Request?.fieldParticipantName: String
    get() = "participantName"