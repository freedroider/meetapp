package com.meetings.android.feature.meetings

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import com.meetings.android.R
import com.meetings.android.core.base.BaseActivity
import com.meetings.android.entity.SortType
import com.meetings.android.feature.addMeeting.AddMeetingActivity
import com.meetings.android.feature.requests.RequestsActivity
import com.meetings.android.utils.*
import kotlinx.android.synthetic.main.activity_meetings.*

class MeetingsActivity : BaseActivity(),
        TabLayout.OnTabSelectedListener {
    override fun obtainLayoutResId() = R.layout.activity_meetings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabLayout.addTab(tabLayout.newTab().setText(SortType.CREATED.type
                .toUpperCase()), true)
        tabLayout.addTab(tabLayout.newTab().setText(SortType.JOINED.type
                .toUpperCase()))
        tabLayout.addTab(tabLayout.newTab().setText(SortType.NEARBY.type
                .toUpperCase()))
        btAddMeeting.clickListener = View.OnClickListener {
            launchActivity<AddMeetingActivity>()
        }
        requestLocationPermissionsIfNeeded()
        ivAppIcon.setOnClickListener {
            launchActivity<RequestsActivity>()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_LOCATION_PERMISSIONS -> if (!isLocationPermissionsGranted(permissions,
                            grantResults)) {
                requestLocationPermissions()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        supportFragmentManager.fragments.forEach {
            it.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        supportFragmentManager.fragments.forEach { fragment ->
            if (fragment is MeetingsFragment) {
                fragment.makeRequest()
            }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        supportFragmentManager.fragments.forEach { fragment ->
            if (fragment is MeetingsFragment) {
                fragment.type = tab!!.text!!.toSortType()
            }
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onResume() {
        super.onResume()
        tabLayout.addOnTabSelectedListener(this)
    }

    override fun onPause() {
        tabLayout.removeOnTabSelectedListener(this)
        super.onPause()
    }
}
