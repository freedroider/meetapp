package com.meetings.android.feature.meetings

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.meetings.android.R
import com.meetings.android.core.base.BaseAdapter
import com.meetings.android.entity.Meeting
import com.meetings.android.utils.resources
import kotlinx.android.synthetic.main.item_meeting.view.*

class MeetingsAdapter : BaseAdapter<Meeting, MeetingsAdapter.ViewHolder>() {
    var itemClickListener: ((Int) -> Unit?)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(inflate(parent, R.layout.item_meeting))

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val meeting = getItem(position)
        viewHolder.fill(meeting)
        viewHolder.itemView.setOnClickListener {
            itemClickListener?.invoke(viewHolder.adapterPosition)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun fill(meeting: Meeting) {
            itemView.tvMeetingName.text = meeting.name
            itemView.tvMeetingCost.text = resources
                    .getString(R.string.euro_price_pattern, meeting.cost)
            itemView.tvMeetingParticipantsCount.text = meeting.participantsIds
                    .size.toString()
            itemView.tvMeetingStatus.text = meeting.status.capitalize()
            itemView.tvMeetingType.text = meeting.type.capitalize()

        }
    }
}