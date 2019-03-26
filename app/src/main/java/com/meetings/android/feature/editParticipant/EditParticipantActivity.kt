package com.meetings.android.feature.editParticipant

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.meetings.android.R
import com.meetings.android.core.base.BaseActivity
import com.meetings.android.entity.User
import com.meetings.android.utils.*
import kotlinx.android.synthetic.main.activity_edit_participant.*
import javax.inject.Inject

const val BUNDLE_EDIT_PARTICIPANT_ID = "bundle.editParticipantId"
const val BUNDLE_EDIT_PARTICIPANT_FULL_NAME = "bundle.editParticipantFullName"
const val BUNDLE_EDIT_PARTICIPANT_RATE = "bundle.editParticipantRate"

class EditParticipantActivity : BaseActivity() {
    @Inject
    lateinit var viewModel: EditParticipantViewModel

    override fun obtainLayoutResId(): Int = R.layout.activity_edit_participant

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId: String = intent.extras?.getString(BUNDLE_EDIT_PARTICIPANT_ID)!!
        viewModel.userLiveData.observe(this, Observer { user ->
            fillUserData(user!!)
        })
        ivBackIcon.setOnClickListener {
            onBackPressed()
        }
        btAddNewParticipant.clickListener = View.OnClickListener {
            if (tiName.checkNonEmpty() && tiCost.checkNonEmpty()) {
                tiName.hideKeyboard()
                val intent = Intent().apply {
                    putExtra(BUNDLE_EDIT_PARTICIPANT_FULL_NAME, tiName.etToString())
                    putExtra(BUNDLE_EDIT_PARTICIPANT_RATE, tiCost.etToDouble())
                    putExtra(BUNDLE_EDIT_PARTICIPANT_ID, userId)
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        viewModel.queryUser(userId)
    }

    private fun fillUserData(user: User) {
        flProgressContainer.gone()
        tiName.isHintAnimationEnabled = false
        etName.setText(user.fullName)
        tiName.isHintAnimationEnabled = true
        tiCost.isHintAnimationEnabled = false
        etCost.setText(user.rate.toString())
        tiCost.isHintAnimationEnabled = true
    }

    companion object {
        fun getEditParticipantIntent(context: Context, user: User): Intent =
                Intent(context, EditParticipantActivity::class.java)
                        .apply {
                            putExtras(Bundle().apply {
                                putString(BUNDLE_EDIT_PARTICIPANT_ID, user.id)
                            })
                        }
    }
}