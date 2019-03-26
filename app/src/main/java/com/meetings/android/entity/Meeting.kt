package com.meetings.android.entity

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.GeoPoint

data class Meeting(@Exclude @set:Exclude @get:Exclude var id: String? = null,
                   var name: String,
                   var location: GeoPoint? = null,
                   var ownerId: String? = null,
                   @Exclude @get:Exclude val participants: ArrayList<User> = ArrayList(),
                   val participantsIds: ArrayList<String> = ArrayList(),
                   var status: String = MeetingStatus.CREATED.status,
                   var type: String = MeetingType.PUBLIC.type,
                   var trackedTime: Long = 0,
                   var cost: Double = 0.0)


enum class SortType(val type: String) {
    CREATED("created"),
    JOINED("joined"),
    NEARBY("nearby")
}

enum class MeetingStatus(val status: String) {
    CREATED("created"),
    IN_PROGRESS("in_progress"),
    STOPPED("stopped")
}

enum class MeetingType(val type: String) {
    PUBLIC("public"),
    PRIVATE("private"),
    HIDDEN("hidden")
}