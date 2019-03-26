package com.meetings.android.feature.addMeeting

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.meetings.android.R
import com.meetings.android.core.base.BaseActivity
import com.meetings.android.entity.Meeting
import com.meetings.android.feature.meeting.MeetingActivity
import com.meetings.android.utils.*
import kotlinx.android.synthetic.main.activity_add_meeting.*
import javax.inject.Inject


class AddMeetingActivity : BaseActivity() {
    @Inject
    lateinit var viewModel: AddMeetingViewModel

    private var snackbar: Snackbar? = null

    override fun obtainLayoutResId() = R.layout.activity_add_meeting

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_LOCATION_RESOLUTION -> {
                when (resultCode) {
                    Activity.RESULT_OK -> insertMeeting()
                    Activity.RESULT_CANCELED -> showLocationSnackbar()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ivBackIcon.setOnClickListener { onBackPressed() }
        viewModel.meetingLiveData.observe(this, Observer {
            btStartMeeting.dismissProgress()
            if (it != null) {
                launchActivity(MeetingActivity.getMeetingIntent(this, it))
                finish()
            }
        })
        viewModel.resolvableApiException.observe(this, Observer {
            it ?: return@Observer
            btStartMeeting.dismissProgress()
            it.startResolutionForResult(this, REQUEST_CODE_LOCATION_RESOLUTION)
        })
        btStartMeeting.clickListener = View.OnClickListener {
            insertMeeting()
        }
    }

    private fun insertMeeting() {
        snackbar?.dismiss()
        if (tiName.checkNonEmpty()) {
            tiName.hideKeyboard()
            btStartMeeting.showProgress()
            val meetingType = rgTypes.checkedText(this)
                    .toMeetingType()
            val meetingName = tiName.etToString()
            val meeting = Meeting(name = meetingName, type = meetingType.type)
            viewModel.insertMeeting(meeting)
        }
    }

    private fun showLocationSnackbar() {
        snackbar = Snackbar.make(coordinatorLayout,
                R.string.location_is_turned_off,
                Snackbar.LENGTH_INDEFINITE)
                .style()
                .setAction(R.string.retry) {
                    insertMeeting()
                }
        snackbar?.show()
    }
}
