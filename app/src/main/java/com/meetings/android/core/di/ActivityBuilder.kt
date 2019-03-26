package com.meetings.android.core.di

import com.meetings.android.feature.addMeeting.AddMeetingActivity
import com.meetings.android.feature.addMeeting.AddMeetingModule
import com.meetings.android.feature.addParticipant.AddParticipantActivity
import com.meetings.android.feature.addParticipant.AddParticipantModule
import com.meetings.android.feature.editMeeting.EditMeetingActivity
import com.meetings.android.feature.editMeeting.EditMeetingModule
import com.meetings.android.feature.editParticipant.EditParticipantActivity
import com.meetings.android.feature.editParticipant.EditParticipantModule
import com.meetings.android.feature.login.LoginActivity
import com.meetings.android.feature.login.LoginModule
import com.meetings.android.feature.meeting.MeetingActivity
import com.meetings.android.feature.meeting.MeetingModule
import com.meetings.android.feature.meetings.MeetingsActivity
import com.meetings.android.feature.meetings.MeetingsModule
import com.meetings.android.feature.requests.RequestsActivity
import com.meetings.android.feature.requests.RequestsModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivityBuilder {
    @ContributesAndroidInjector(modules = [LoginModule::class])
    fun bindLoginActivityInjector(): LoginActivity

    @ContributesAndroidInjector(modules = [MeetingsModule::class])
    fun bindMeetingsActivityInjector(): MeetingsActivity

    @ContributesAndroidInjector(modules = [MeetingModule::class])
    fun bindMeetingActivityInjector(): MeetingActivity

    @ContributesAndroidInjector(modules = [AddMeetingModule::class])
    fun bindAddMeetingActivityInjector(): AddMeetingActivity

    @ContributesAndroidInjector(modules = [EditMeetingModule::class])
    fun bindEditMeetingActivityInjector(): EditMeetingActivity

    @ContributesAndroidInjector(modules = [AddParticipantModule::class])
    fun bindAddParticipantActivityInjector(): AddParticipantActivity

    @ContributesAndroidInjector(modules = [EditParticipantModule::class])
    fun bindEditParticipantActivityInjector(): EditParticipantActivity

    @ContributesAndroidInjector(modules = [RequestsModule::class])
    fun bindRequestsActivityInjector(): RequestsActivity
}