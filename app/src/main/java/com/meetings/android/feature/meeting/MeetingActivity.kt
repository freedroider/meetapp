package com.meetings.android.feature.meeting

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.meetings.android.R
import com.meetings.android.core.base.BaseActivity
import com.meetings.android.entity.Meeting
import com.meetings.android.entity.User
import com.meetings.android.feature.addParticipant.AddParticipantActivity
import com.meetings.android.feature.addParticipant.BUNDLE_ADD_PARTICIPANT_FULL_NAME
import com.meetings.android.feature.addParticipant.BUNDLE_ADD_PARTICIPANT_RATE
import com.meetings.android.feature.editMeeting.BUNDLE_EDIT_MEETING_NAME
import com.meetings.android.feature.editMeeting.BUNDLE_EDIT_MEETING_TYPE
import com.meetings.android.feature.editMeeting.EditMeetingActivity
import com.meetings.android.feature.editParticipant.BUNDLE_EDIT_PARTICIPANT_FULL_NAME
import com.meetings.android.feature.editParticipant.BUNDLE_EDIT_PARTICIPANT_ID
import com.meetings.android.feature.editParticipant.BUNDLE_EDIT_PARTICIPANT_RATE
import com.meetings.android.feature.editParticipant.EditParticipantActivity
import com.meetings.android.feature.meetings.MeetingsActivity
import com.meetings.android.utils.*
import com.meetings.android.view.DividerDecoration
import kotlinx.android.synthetic.main.activity_meeting.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


private const val BUNDLE_MEETING_ID = "bundle.meetingId"
private const val REQUEST_ID_ADD_PARTICIPANT = 303
private const val REQUEST_ID_EDIT_MEETING = 304
private const val REQUEST_ID_EDIT_PARTICIPANT = 305

class MeetingActivity : BaseActivity() {
    @Inject
    lateinit var meetingViewModel: MeetingViewModel

    private lateinit var adapter: ParticipantsAdapter

    private val meetingInitializer by lazy {
        flProgressContainer.gone()
        val meeting = meetingViewModel.meetingLiveData.value!!
        if (isNeedStartTracking(meeting)) {
            meetingViewModel.startTracking(this@MeetingActivity, meeting)
        }
        val amIOwner = meeting.amIOwner(meetingViewModel.getUserId())
        adapter.removedListener = { position ->
            meetingViewModel.removeParticipant(adapter.remove(position))
        }
        adapter.editedListener = { position ->
            val user = adapter.getItem(position)
            launchActivityForResult<EditParticipantActivity>(REQUEST_ID_EDIT_PARTICIPANT,
                    EditParticipantActivity.getEditParticipantIntent(this, user))
            adapter.notifyItemChanged(position)
        }
        adapter.ownerId = meeting.ownerId!!
        adapter.editable = amIOwner
        ivEdit.visibility = if (amIOwner) View.VISIBLE else View.GONE
        ivEdit.setOnClickListener {
            launchActivityForResult<EditMeetingActivity>(REQUEST_ID_EDIT_MEETING,
                    EditMeetingActivity.getEditMeetingIntent(this, meeting))
        }
        tvAddNewParticipant.visibility = if (amIOwner) View.VISIBLE else View.GONE
        tvAddNewParticipant.setOnClickListener {
            launchActivityForResult<AddParticipantActivity>(REQUEST_ID_ADD_PARTICIPANT)
        }
        return@lazy
    }

    override fun obtainLayoutResId() = R.layout.activity_meeting

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            RESULT_OK -> {
                when (requestCode) {
                    REQUEST_ID_ADD_PARTICIPANT -> addParticipant(data)
                    REQUEST_ID_EDIT_MEETING -> updateMeeting(data)
                    REQUEST_ID_EDIT_PARTICIPANT -> updateParticipant(data)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ParticipantsAdapter()
        ivBackIcon.setOnClickListener {
            onBackPressed()
        }
        rvParticipants.layoutManager = LinearLayoutManager(this)
        rvParticipants.addItemDecoration(DividerDecoration(this, R.color.grey))
        rvParticipants.adapter = adapter
        val meetingId: String = intent.extras?.getString(BUNDLE_MEETING_ID)!!
        meetingViewModel.meetingLiveData.observe(this, Observer { meeting ->
            meetingInitializer.apply {
                meeting!!.apply {
                    fillMeetingData(this)
                    updateMeetingStatus(this)
                }
            }
        })
        meetingViewModel.participantsLiveData.observe(this, Observer { participants ->
            adapter.replace(participants!!)
        })
        meetingViewModel.queryMeeting(meetingId)
    }

    override fun onBackPressed() {
        launchActivity<MeetingsActivity> {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        finish()
    }

    override fun onDestroy() {
        if (isFinishing) {
            meetingViewModel.stopTracking(this)
        }
        super.onDestroy()
    }

    private fun fillMeetingData(meeting: Meeting) {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(meeting.trackedTime).toInt()
        tvHeader.text = meeting.name
        tvCurrentMeetingCost.text = getString(R.string.euro_price_pattern,
                meeting.cost)
        tvCurrentMeetingTrackedTime.text = resources
                .getQuantityString(R.plurals.minutes, minutes, minutes)
    }

    private fun updateMeetingStatus(meeting: Meeting) {
        val userId = meetingViewModel.getUserId()
        if (meeting.amIOwner(userId)) {
            updateOwnerMeetingStatus(meeting)
        } else if (!meeting.amIParticipant(userId)) {
            updateJoinMeetingStatus(meeting)
        } else {
            btMeetingAction.gone()
        }
    }

    private fun updateJoinMeetingStatus(meeting: Meeting) {
        when {
            isPublic(meeting) -> {
                btMeetingAction.visible()
                btMeetingAction.text = getString(R.string.join_meeting)
                btMeetingAction.icon = R.drawable.ic_add
                btMeetingAction.setColor(ContextCompat.getColor(this,
                        R.color.colorAccent))
                btMeetingAction.clickListener = View.OnClickListener {
                    btMeetingAction.showProgress()
                    meetingViewModel.joinToMeeting()
                }
            }
            isPrivate(meeting) -> {
                meetingViewModel.requestLiveData.observe(this, Observer { request ->
                    if (request == null) {
                        btMeetingAction.visible()
                        btMeetingAction.text = getString(R.string.send_request)
                        btMeetingAction.icon = R.drawable.ic_add
                        btMeetingAction.setColor(ContextCompat.getColor(this,
                                R.color.colorAccent))
                        btMeetingAction.clickListener = View.OnClickListener {
                            btMeetingAction.showProgress()
                            meetingViewModel.sendRequest()
                        }
                    } else {
                        if (btMeetingAction.isProgressShow) {
                            btMeetingAction.dismissProgress()
                        }
                        btMeetingAction.gone()
                        showToast("Request for the meeting was sent")
                    }
                })
                meetingViewModel.doesRequestExist()
            }
        }
    }

    private fun updateOwnerMeetingStatus(meeting: Meeting) {
        when {
            isInProgress(meeting) -> {
                btMeetingAction.visible()
                btMeetingAction.text = getString(R.string.stop_meeting)
                btMeetingAction.icon = R.drawable.ic_stop
                btMeetingAction.setColor(ContextCompat.getColor(this,
                        R.color.rbColorActivated))
                btMeetingAction.clickListener = View.OnClickListener {
                    meetingViewModel.stopTracking(this)
                }
            }
            isCreated(meeting) -> {
                btMeetingAction.visible()
                btMeetingAction.text = getString(R.string.start_meeting)
                btMeetingAction.icon = R.drawable.ic_play
                btMeetingAction.setColor(ContextCompat.getColor(this,
                        R.color.colorAccent))
                btMeetingAction.clickListener = View.OnClickListener {
                    meetingViewModel.startTracking(this, meeting)
                }
            }
            isStopped(meeting) -> {
                btMeetingAction.visible()
                btMeetingAction.text = getString(R.string.continue_meeting)
                btMeetingAction.icon = R.drawable.ic_continue
                btMeetingAction.setColor(ContextCompat.getColor(this,
                        R.color.colorAccent))
                btMeetingAction.clickListener = View.OnClickListener {
                    meetingViewModel.startTracking(this, meeting)
                }
            }
        }
        btMeetingAction.tag = meeting.status
    }

    private fun isPublic(meeting: Meeting) = meeting.isPublic && (btMeetingAction.tag == null
            || meeting.type != btMeetingAction.tag as String)

    private fun isPrivate(meeting: Meeting) = meeting.isPrivate && (btMeetingAction.tag == null
            || meeting.type != btMeetingAction.tag as String)

    private fun isNeedStartTracking(meeting: Meeting) = (meeting.isCreated || meeting.isInProgress)
            && meeting.amIOwner(meetingViewModel.getUserId())
            && !meetingViewModel.isTrackingStarted()

    private fun isInProgress(meeting: Meeting) = meeting.isInProgress &&
            (btMeetingAction.tag == null
                    || meeting.status != btMeetingAction.tag as String)

    private fun isStopped(meeting: Meeting) = meeting.isStopped &&
            (btMeetingAction.tag == null
                    || meeting.status != btMeetingAction.tag as String)

    private fun isCreated(meeting: Meeting) = meeting.isCreated &&
            (btMeetingAction.tag == null
                    || meeting.status != btMeetingAction.tag as String)

    private fun addParticipant(data: Intent?) {
        data?.apply {
            val fullName = data.getStringExtra(BUNDLE_ADD_PARTICIPANT_FULL_NAME)
            val rate = data.getDoubleExtra(BUNDLE_ADD_PARTICIPANT_RATE, 0.0)
            val user = User(fullName = fullName, rate = rate)
            meetingViewModel.addParticipant(user)
        }
    }

    private fun updateMeeting(data: Intent?) {
        data?.apply {
            val meetingType = data.getStringExtra(BUNDLE_EDIT_MEETING_TYPE)
                    .toMeetingType().type
            val meetingName = data.getStringExtra(BUNDLE_EDIT_MEETING_NAME)
            meetingViewModel.updateMeeting(meetingType, meetingName)
        }
    }

    private fun updateParticipant(data: Intent?) {
        data?.apply {
            val participantId = data.getStringExtra(BUNDLE_EDIT_PARTICIPANT_ID)
            val participantFullName = data.getStringExtra(BUNDLE_EDIT_PARTICIPANT_FULL_NAME)
            val participantRate = data.getDoubleExtra(BUNDLE_EDIT_PARTICIPANT_RATE, 0.0)
            adapter.notifyUpdate(participantId)
            meetingViewModel.updateParticipant(participantId, participantFullName, participantRate)
        }
    }

    companion object {
        fun getMeetingIntent(context: Context, meeting: Meeting): Intent =
                Intent(context, MeetingActivity::class.java)
                        .apply {
                            putExtras(Bundle().apply {
                                putString(BUNDLE_MEETING_ID, meeting.id)
                            })
                        }
    }
}