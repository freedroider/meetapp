package com.meetings.android.feature.editParticipant

import android.arch.lifecycle.MutableLiveData
import com.meetings.android.core.base.BaseViewModel
import com.meetings.android.entity.User
import com.meetings.android.model.firestore.Firestore
import com.meetings.android.model.preferences.LocalPreferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class EditParticipantViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var firestore: Firestore
    @Inject
    lateinit var localPreferences: LocalPreferences

    val userLiveData: MutableLiveData<User> = MutableLiveData()

    fun queryUser(userId: String) {
        firestore.queryUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ user ->
                    userLiveData.value = user
                }, {
                    it.printStackTrace()
                })
    }
}