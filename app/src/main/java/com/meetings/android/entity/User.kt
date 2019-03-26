package com.meetings.android.entity

import com.google.firebase.firestore.Exclude

data class User(@Exclude @set:Exclude @get:Exclude var id: String? = null,
                var fullName: String,
                var rate: Double)