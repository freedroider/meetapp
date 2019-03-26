package com.meetings.android.utils

import android.content.res.Resources
import com.meetings.android.entity.MeetingType
import com.meetings.android.entity.SortType

fun Long.msToHours() = this / 1_000.0 / 60.0 / 60.0

fun CharSequence.toSortType(): SortType {
    val type = toString().toLowerCase()
    SortType.values().forEach {
        if (it.type == type) {
            return it
        }
    }
    throw IllegalArgumentException()
}

fun CharSequence.toMeetingType(): MeetingType {
    val type = toString().toLowerCase()
    MeetingType.values().forEach {
        if (it.type == type) {
            return it
        }
    }
    throw IllegalArgumentException()
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()