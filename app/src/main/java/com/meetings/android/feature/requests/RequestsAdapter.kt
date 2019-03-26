package com.meetings.android.feature.requests

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.meetings.android.R
import com.meetings.android.core.base.BaseAdapter
import com.meetings.android.entity.Request
import com.meetings.android.utils.formatCreatedAt
import kotlinx.android.synthetic.main.item_request.view.*


class RequestsAdapter : BaseAdapter<Request, RequestsAdapter.ViewHolder>() {
    var acceptListner: ((Int) -> Unit?)? = null
    var declineListener: ((Int) -> Unit?)? = null

    override fun onBindViewHolder(viewHolder: RequestsAdapter.ViewHolder, position: Int) {
        val request = getItem(position)
        viewHolder.fill(request)
        viewHolder.itemView.btRequestAccept.clickListener = View.OnClickListener {
            acceptListner?.invoke(viewHolder.adapterPosition)
        }
        viewHolder.itemView.btRequestDecline.clickListener = View.OnClickListener {
            declineListener?.invoke(viewHolder.adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder =
            ViewHolder(inflate(parent, R.layout.item_request))

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun fill(request: Request) {
            itemView.tvRequestMeetingRoomName.text = request.meetingName
            itemView.tvRequestParticipantName.text = request.participantName
            itemView.tvRequestDate.text = request.formatCreatedAt()
        }
    }
}