package com.meetings.android.core.di

import android.arch.lifecycle.ViewModel
import com.meetings.android.feature.addMeeting.AddMeetingViewModel
import com.meetings.android.feature.login.LoginViewModel
import com.meetings.android.feature.meeting.MeetingViewModel
import com.meetings.android.feature.meetings.MeetingsViewModel
import com.meetings.android.feature.meetings.MeetingsFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    fun bindLoginViewModel(viewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MeetingsViewModel::class)
    fun bindMeetingsViewModel(viewModel: MeetingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MeetingViewModel::class)
    fun bindMeetingViewModel(viewModel: MeetingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddMeetingViewModel::class)
    fun bindAddMeetingViewModel(viewModel: AddMeetingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MeetingsFragmentViewModel::class)
    fun bindCreatedMeetingViewModel(viewModel: MeetingsFragmentViewModel): ViewModel
}
