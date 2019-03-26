package com.meetings.android.feature.editMeeting

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.meetings.android.R
import com.meetings.android.core.base.BaseActivity
import com.meetings.android.entity.Meeting
import com.meetings.android.entity.MeetingType.*
import com.meetings.android.utils.*
import kotlinx.android.synthetic.main.activity_edit_meeting.*
import javax.inject.Inject

const val BUNDLE_EDIT_MEETING_ID = "bundle.editMeetingId"
const val BUNDLE_EDIT_MEETING_TYPE = "bundle.editMeetingType"
const val BUNDLE_EDIT_MEETING_NAME = "bundle.editMeetingName"

class EditMeetingActivity : BaseActivity() {
    @Inject
    lateinit var viewModel: EditMeetingViewModel

    override fun obtainLayoutResId() = R.layout.activity_edit_meeting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ivBackIcon.setOnClickListener { onBackPressed() }
        val meetingId: String = intent.extras?.getString(BUNDLE_EDIT_MEETING_ID)!!
        viewModel.meetingLiveData.observe(this, Observer { meeting ->
            fillMeetingData(meeting!!)
        })
        btUpdateMeeting.clickListener = View.OnClickListener {
            if (tiName.checkNonEmpty()) {
                val meetingType = rgTypes.checkedText(this)
                val meetingName = tiName.etToString()
                val intent = Intent().apply {
                    putExtra(BUNDLE_EDIT_MEETING_TYPE, meetingType)
                    putExtra(BUNDLE_EDIT_MEETING_NAME, meetingName)
                }
                setResult(RESULT_OK, intent)
                finish()
            }
        }
        viewModel.queryMeeting(meetingId)
    }

    private fun fillMeetingData(meeting: Meeting) {
        flProgressContainer.gone()
        tiName.isHintAnimationEnabled = false
        etName.setText(meeting.name)
        tiName.isHintAnimationEnabled = true
        val meetingType = meeting.type.toMeetingType()
        when (meetingType) {
            PUBLIC -> {
                rbPublic.isChecked = true
                rbPublic.jumpDrawablesToCurrentState()
            }
            PRIVATE -> {
                rbPrivate.isChecked = true
                rbPrivate.jumpDrawablesToCurrentState()
            }
            HIDDEN -> {
                rbHidden.isChecked = true
                rbHidden.jumpDrawablesToCurrentState()
            }
        }
    }

    companion object {
        fun getEditMeetingIntent(context: Context, meeting: Meeting): Intent =
                Intent(context, EditMeetingActivity::class.java)
                        .apply {
                            putExtras(Bundle().apply {
                                putString(BUNDLE_EDIT_MEETING_ID, meeting.id)
                            })
                        }
    }
}
