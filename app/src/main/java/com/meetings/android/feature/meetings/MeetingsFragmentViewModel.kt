package com.meetings.android.feature.meetings

import android.arch.lifecycle.MutableLiveData
import android.location.Location
import com.google.android.gms.common.api.ResolvableApiException
import com.meetings.android.core.base.BaseViewModel
import com.meetings.android.entity.Meeting
import com.meetings.android.model.firestore.Firestore
import com.meetings.android.model.location.LocationSource
import com.meetings.android.model.preferences.LocalPreferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MeetingsFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var firestore: Firestore
    @Inject
    lateinit var locationSource: LocationSource
    @Inject
    lateinit var localPreferences: LocalPreferences

    val meetingLiveData: MutableLiveData<List<Meeting>> = MutableLiveData()
    val resolvableApiException: MutableLiveData<ResolvableApiException> = MutableLiveData()

    private var disposable: Disposable? = null

    fun queryNearbyMeetings() {
        disposable?.dispose()
        var location: Location? = null
        disposable = locationSource.getLastKnownLocation()
                .map {
                    location = it
                }
                .flatMap {
                    firestore.queryUser(localPreferences.getUserId()!!)
                }
                .flatMap { user ->
                    firestore.queryNearbyMeetings(user, location!!)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ savedMeeting -> meetingLiveData.value = savedMeeting }, {
                    it.printStackTrace()
                    if (it is ResolvableApiException) {
                        resolvableApiException.postValue(it)
                    }
                })
    }

    fun queryCreatedMeetings() {
        disposable?.dispose()
        disposable = firestore.queryUser(localPreferences.getUserId()!!)
                .flatMap {
                    firestore.queryCreatedMeetings(it)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ savedMeeting -> meetingLiveData.value = savedMeeting }, {
                    it.printStackTrace()
                })
    }

    fun queryJoinedMeetings() {
        disposable?.dispose()
        disposable = firestore.queryUser(localPreferences.getUserId()!!)
                .flatMap {
                    firestore.queryJoinedMeetings(it)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ savedMeeting -> meetingLiveData.value = savedMeeting }, {
                    it.printStackTrace()
                })
    }
}