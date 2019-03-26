package com.meetings.android.model.firestore

import android.location.Location
import com.meetings.android.entity.Meeting
import com.meetings.android.entity.Request
import com.meetings.android.entity.User
import io.reactivex.Single

interface Firestore {
    fun queryUser(user: User): Single<User>

    fun queryUser(userId: String): Single<User>

    fun queryMeeting(meetingId: String): Single<Meeting>

    fun queryCreatedMeetings(user: User): Single<List<Meeting>>

    fun queryJoinedMeetings(user: User): Single<List<Meeting>>

    fun queryNearbyMeetings(user: User, location: Location): Single<List<Meeting>>

    fun queryRequest(meeting: Meeting, userId: String): Single<Request>

    fun queryRequests(ownerId: String): Single<List<Request>>

    fun insertUser(user: User): Single<User>

    fun insertMeeting(meeting: Meeting): Single<Meeting>

    fun insertRequest(request: Request): Single<Request>

    fun updateMeeting(meeting: Meeting): Single<Meeting>

    fun updateUser(user: User): Single<User>

    fun deleteRequest(request: Request): Single<Request>
}