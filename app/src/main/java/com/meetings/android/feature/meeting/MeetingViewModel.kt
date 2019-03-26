package com.meetings.android.feature.meeting

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.meetings.android.core.base.BaseViewModel
import com.meetings.android.entity.Meeting
import com.meetings.android.entity.Request
import com.meetings.android.entity.User
import com.meetings.android.model.firestore.Firestore
import com.meetings.android.model.preferences.LocalPreferences
import com.meetings.android.model.tracker.Tracker
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MeetingViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var tracker: Tracker
    @Inject
    lateinit var firestore: Firestore
    @Inject
    lateinit var localPreferences: LocalPreferences

    val meetingLiveData: MutableLiveData<Meeting> = MutableLiveData()
    val participantsLiveData: MutableLiveData<List<User>> = MutableLiveData()
    val requestLiveData: MutableLiveData<Request> = MutableLiveData()

    private var disposable: Disposable? = null

    override fun onCleared() {
        disposable?.dispose()
        disposable = null
    }

    fun startTracking(context: Context, meeting: Meeting) {
        disposable = tracker.startTracking(context, meeting)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    meetingLiveData.value = meeting
                }
    }

    fun isTrackingStarted() = disposable != null

    fun stopTracking(context: Context) {
        tracker.stopTracking(context)
        disposable?.dispose()
        disposable = null
        meetingLiveData.postValue(meetingLiveData.value)
    }

    fun queryMeeting(meetingId: String) {
        firestore.queryMeeting(meetingId)
                .flatMap { meeting ->
                    queryParticipants(meeting)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ meeting ->
                    participantsLiveData.value = meeting.participants
                    meetingLiveData.value = meeting
                }, {
                    it.printStackTrace()
                })
    }

    fun updateMeeting(meetingType: String, meetingName: String) {
        meetingLiveData.value!!.type = meetingType
        meetingLiveData.value!!.name = meetingName
        firestore.updateMeeting(meetingLiveData.value!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ meeting ->
                    meetingLiveData.value = meeting
                }, {
                    it.printStackTrace()
                })
    }

    fun getUserId() = localPreferences.getUserId()

    fun addParticipant(user: User) {
        firestore.insertUser(user)
                .flatMap { savedUser ->
                    meetingLiveData.value!!.participantsIds.add(savedUser.id!!)
                    meetingLiveData.value!!.participants.add(savedUser)
                    firestore.updateMeeting(meetingLiveData.value!!)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ meeting ->
                    participantsLiveData.value = meeting.participants
                }, {
                    it.printStackTrace()
                })
    }

    fun removeParticipant(user: User) {
        meetingLiveData.value!!.participantsIds.remove(user.id)
        meetingLiveData.value!!.participants.remove(user)
        meetingLiveData.postValue(meetingLiveData.value)
        firestore.updateMeeting(meetingLiveData.value!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ meeting ->
                    meetingLiveData.value = meeting
                    meetingLiveData.postValue(meetingLiveData.value)
                }, {
                    it.printStackTrace()
                })
    }

    fun updateParticipant(participantId: String,
                          participantFullName: String,
                          participantRate: Double) {
        val user = meetingLiveData.value!!.participants.find {
            it.id == participantId
        }
        user!!.fullName = participantFullName
        user.rate = participantRate
        firestore.updateUser(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                }, {
                    it.printStackTrace()
                })

    }

    fun joinToMeeting() {
        val userId = getUserId()!!
        firestore.queryUser(userId)
                .flatMap { user ->
                    meetingLiveData.value!!.participantsIds.add(user.id!!)
                    meetingLiveData.value!!.participants.add(user)
                    firestore.updateMeeting(meetingLiveData.value!!)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ meeting ->
                    participantsLiveData.value = meeting.participants
                    meetingLiveData.postValue(meetingLiveData.value)
                }, {
                    it.printStackTrace()
                })
    }

    fun sendRequest() {
        val request = Request(meetingId = meetingLiveData.value!!.id!!,
                meetingName = meetingLiveData.value!!.name,
                ownerId = meetingLiveData.value!!.ownerId!!,
                participantId = getUserId()!!)
        firestore.queryUser(getUserId()!!)
                .flatMap {
                    request.participantName = it.fullName
                    Single.just(request)
                }
                .flatMap {
                    firestore.insertRequest(request)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    requestLiveData.value = it
                }, {
                    it.printStackTrace()
                })
    }

    fun doesRequestExist() {
        val userId = getUserId()!!
        firestore.queryRequest(meetingLiveData.value!!, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ request ->
                    requestLiveData.value = request
                }, {
                    it.printStackTrace()
                    requestLiveData.value = null
                })
    }

    private fun queryParticipants(meeting: Meeting): Single<Meeting>? {
        return Flowable.just(meeting.participantsIds)
                .flatMapIterable { participantsIds ->
                    participantsIds
                }
                .flatMap { participantId ->
                    firestore.queryUser(participantId)
                            .toFlowable()
                }
                .map { user ->
                    meeting.participants.add(user)
                }
                .toList()
                .map {
                    meeting.participants
                            .sortWith(Comparator { user1: User, user2: User ->
                                when (meeting.ownerId) {
                                    user1.id -> -1
                                    user2.id -> 1
                                    else -> 0
                                }
                            })
                    meeting
                }
                .flatMap {
                    Single.just(meeting)
                }
    }
}