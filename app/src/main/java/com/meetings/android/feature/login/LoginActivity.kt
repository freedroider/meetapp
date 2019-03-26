package com.meetings.android.feature.login

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.meetings.android.R
import com.meetings.android.core.base.BaseActivity
import com.meetings.android.entity.User
import com.meetings.android.feature.meetings.MeetingsActivity
import com.meetings.android.utils.*
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : BaseActivity() {
    @Inject
    lateinit var viewModel: LoginViewModel

    override fun obtainLayoutResId() = R.layout.activity_login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.isUserLoggedIn()) {
            launchActivity<MeetingsActivity>()
            finish()
            return
        }
        viewModel.userLiveData.observe(this, Observer {
            if (it != null) {
                btSave.dismissProgress()
                launchActivity<MeetingsActivity>()
                finish()
            }
        })
        btSave.clickListener = View.OnClickListener {
            if (tiName.checkNonEmpty() && tiCost.checkNonEmpty()) {
                tiName.hideKeyboard()
                btSave.showProgress()
                val user = User(fullName = tiName.etToString(), rate = tiCost.etToDouble())
                viewModel.loginUser(user)
            }
        }
    }
}