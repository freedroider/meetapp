package com.meetings.android.feature.requests

import android.arch.lifecycle.MutableLiveData
import com.meetings.android.core.base.BaseViewModel
import com.meetings.android.entity.Request
import com.meetings.android.model.firestore.Firestore
import com.meetings.android.model.preferences.LocalPreferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RequestsViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var firestore: Firestore
    @Inject
    lateinit var localPreferences: LocalPreferences

    val requestsLiveData: MutableLiveData<List<Request>> = MutableLiveData()

    fun queryRequests() {
        firestore.queryRequests(localPreferences.getUserId()!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ requests -> requestsLiveData.value = requests }, {
                    it.printStackTrace()
                })
    }

    fun acceptRequest(request: Request) {
        firestore.queryMeeting(request.meetingId)
                .flatMap { meeting ->
                    meeting.participantsIds.add(request.participantId)
                    firestore.updateMeeting(meeting = meeting)
                }
                .flatMap {
                    firestore.deleteRequest(request)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, { it.printStackTrace() })
    }

    fun declineRequest(request: Request) {
        firestore.deleteRequest(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, { it.printStackTrace() })
    }
}