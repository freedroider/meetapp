package com.meetings.android.feature.addMeeting

import android.arch.lifecycle.MutableLiveData
import com.google.android.gms.common.api.ResolvableApiException
import com.google.firebase.firestore.GeoPoint
import com.meetings.android.core.base.BaseViewModel
import com.meetings.android.entity.Meeting
import com.meetings.android.model.firestore.Firestore
import com.meetings.android.model.location.LocationSource
import com.meetings.android.model.preferences.LocalPreferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AddMeetingViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var firestore: Firestore
    @Inject
    lateinit var localPreferences: LocalPreferences
    @Inject
    lateinit var locationSource: LocationSource

    val meetingLiveData: MutableLiveData<Meeting> = MutableLiveData()
    val resolvableApiException: MutableLiveData<ResolvableApiException> = MutableLiveData()

    fun insertMeeting(meeting: Meeting) {
        locationSource.getLastKnownLocation()
                .map { location ->
                    meeting.location = GeoPoint(location.latitude, location.longitude)
                }
                .flatMap {
                    firestore.queryUser(localPreferences.getUserId()!!)
                }.map { user ->
                    meeting.participantsIds.add(user.id!!)
                    meeting.ownerId = user.id
                    meeting
                }.flatMap { createdMeeting ->
                    firestore.insertMeeting(createdMeeting)
                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ savedMeeting -> meetingLiveData.value = savedMeeting }, {
                    it.printStackTrace()
                    if (it is ResolvableApiException) {
                        resolvableApiException.postValue(it)
                    }
                })
    }
}