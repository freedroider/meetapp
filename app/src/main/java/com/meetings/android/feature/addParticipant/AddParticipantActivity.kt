package com.meetings.android.feature.addParticipant

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.meetings.android.R
import com.meetings.android.core.base.BaseActivity
import com.meetings.android.utils.checkNonEmpty
import com.meetings.android.utils.etToDouble
import com.meetings.android.utils.etToString
import com.meetings.android.utils.hideKeyboard
import kotlinx.android.synthetic.main.activity_add_participant.*


const val BUNDLE_ADD_PARTICIPANT_FULL_NAME = "bundle.addParticipantFullName"
const val BUNDLE_ADD_PARTICIPANT_RATE = "bundle.addParticipantRate"

class AddParticipantActivity : BaseActivity() {
    override fun obtainLayoutResId(): Int = R.layout.activity_add_participant

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ivBackIcon.setOnClickListener {
            onBackPressed()
        }
        btAddNewParticipant.clickListener = View.OnClickListener {
            if (tiName.checkNonEmpty() && tiCost.checkNonEmpty()) {
                tiName.hideKeyboard()
                val intent = Intent().apply {
                    putExtra(BUNDLE_ADD_PARTICIPANT_FULL_NAME, tiName.etToString())
                    putExtra(BUNDLE_ADD_PARTICIPANT_RATE, tiCost.etToDouble())
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}