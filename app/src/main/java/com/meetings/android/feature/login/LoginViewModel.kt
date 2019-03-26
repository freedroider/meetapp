package com.meetings.android.feature.login

import android.arch.lifecycle.MutableLiveData
import com.meetings.android.core.base.BaseViewModel
import com.meetings.android.entity.User
import com.meetings.android.model.firestore.Firestore
import com.meetings.android.model.preferences.LocalPreferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LoginViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var firestore: Firestore
    @Inject
    lateinit var localPreferences: LocalPreferences

    val userLiveData: MutableLiveData<User> = MutableLiveData()

    fun loginUser(user: User) {
        firestore.queryUser(user)
                .onErrorResumeNext(firestore.insertUser(user))
                .map {
                    localPreferences.saveUserId(it.id)
                    it
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ savedUser ->
                    userLiveData.value = savedUser
                }, {
                    it.printStackTrace()
                })
    }

    fun isUserLoggedIn() = localPreferences.getUserId() != null
}