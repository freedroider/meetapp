package com.meetings.android.feature.meetings

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.meetings.android.R
import com.meetings.android.core.base.BaseFragment
import com.meetings.android.entity.SortType
import com.meetings.android.feature.meeting.MeetingActivity
import com.meetings.android.utils.*
import com.meetings.android.view.DividerDecoration
import kotlinx.android.synthetic.main.activity_meetings.*
import kotlinx.android.synthetic.main.fragment_meetings.*
import javax.inject.Inject

class MeetingsFragment : BaseFragment() {
    @Inject
    lateinit var viewModel: MeetingsFragmentViewModel

    private lateinit var adapter: MeetingsAdapter
    private var snackbar: Snackbar? = null

    var type: SortType = SortType.CREATED
        set(value) {
            field = value
            if (isInited) {
                makeRequest()
            }
            snackbar ?: return
            snackbar!!.dismiss()
            snackbar = null
        }

    override fun obtainLayoutResId(): Int = R.layout.fragment_meetings

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            activity.REQUEST_CODE_LOCATION_RESOLUTION -> {
                when (resultCode) {
                    Activity.RESULT_OK -> type = type
                    Activity.RESULT_CANCELED -> showLocationSnackbar()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = MeetingsAdapter()
        viewModel.meetingLiveData.observe(this, Observer {
            it ?: return@Observer
            adapter.replace(it)
            adapter.itemClickListener = { position ->
                val meeting = adapter.getItem(position)
                launchActivity(MeetingActivity.getMeetingIntent(context!!, meeting))
            }
            progressBar.gone()
        })
        viewModel.resolvableApiException.observe(this, Observer {
            it ?: return@Observer
            progressBar.gone()
            it.startResolutionForResult(activity, activity.REQUEST_CODE_LOCATION_RESOLUTION)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvMeetings.layoutManager = LinearLayoutManager(context)
        rvMeetings.adapter = adapter
        rvMeetings.addItemDecoration(DividerDecoration(context!!, R.color.grey))
        makeRequest()
    }

    fun makeRequest() {
        progressBar.visible()
        when (type) {
            SortType.CREATED -> viewModel.queryCreatedMeetings()
            SortType.JOINED -> viewModel.queryJoinedMeetings()
            SortType.NEARBY -> viewModel.queryNearbyMeetings()
        }
        adapter.clear()
    }

    private fun showLocationSnackbar() {
        if (activity is MeetingsActivity) {
            snackbar = Snackbar.make(activity!!.coordinatorLayout,
                    R.string.location_is_turned_off,
                    Snackbar.LENGTH_INDEFINITE)
                    .style()
                    .setAction(R.string.retry) {
                        viewModel.queryNearbyMeetings()
                    }
        }
        snackbar?.show()
    }
}