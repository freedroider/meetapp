package com.meetings.android.entity

import com.google.firebase.firestore.Exclude
import java.util.*

data class Request(@Exclude @set:Exclude @get:Exclude var id: String? = null,
                   val createdAt: Date = Date(),
                   val meetingId: String,
                   val meetingName: String,
                   val ownerId: String,
                   val participantId: String,
                   var participantName: String? = null)