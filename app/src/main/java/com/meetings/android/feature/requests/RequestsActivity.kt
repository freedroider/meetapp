package com.meetings.android.feature.requests

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.meetings.android.R
import com.meetings.android.core.base.BaseActivity
import com.meetings.android.utils.gone
import com.meetings.android.utils.visible
import com.meetings.android.view.DividerDecoration
import kotlinx.android.synthetic.main.activity_requests.*
import javax.inject.Inject

class RequestsActivity : BaseActivity() {

    @Inject
    lateinit var requestsViewModel: RequestsViewModel

    private lateinit var adapter: RequestsAdapter

    override fun obtainLayoutResId(): Int = R.layout.activity_requests

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestsViewModel.requestsLiveData.observe(this, Observer {
            it ?: return@Observer
            progressBar.gone()
            adapter.replace(it)
        })
        adapter = RequestsAdapter()
        adapter.acceptListner = { position ->
            val request = adapter.getItem(position)
            adapter.remove(position)
            requestsViewModel.acceptRequest(request)
        }
        adapter.declineListener = { position ->
            val request = adapter.getItem(position)
            adapter.remove(position)
            requestsViewModel.declineRequest(request)
        }
        rvRequests.layoutManager = LinearLayoutManager(this)
        rvRequests.adapter = adapter
        rvRequests.addItemDecoration(DividerDecoration(this, R.color.grey))
        ivBackIcon.setOnClickListener { onBackPressed() }
        progressBar.visible()
        requestsViewModel.queryRequests()
    }
}