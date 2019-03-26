package com.meetings.android.feature.editMeeting

import android.arch.lifecycle.MutableLiveData
import com.meetings.android.core.base.BaseViewModel
import com.meetings.android.entity.Meeting
import com.meetings.android.model.firestore.Firestore
import com.meetings.android.model.preferences.LocalPreferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class EditMeetingViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var firestore: Firestore
    @Inject
    lateinit var localPreferences: LocalPreferences

    val meetingLiveData: MutableLiveData<Meeting> = MutableLiveData()

    fun queryMeeting(meetingId: String) {
        firestore.queryMeeting(meetingId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ meeting ->
                    meetingLiveData.value = meeting
                }, {
                    it.printStackTrace()
                })
    }
}