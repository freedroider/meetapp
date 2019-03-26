package com.meetings.android.feature.meeting

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.meetings.android.R
import com.meetings.android.core.base.BaseAdapter
import com.meetings.android.entity.User
import com.meetings.android.utils.resources
import kotlinx.android.synthetic.main.item_participant.view.*
import kotlinx.android.synthetic.main.item_participant_editable.view.*
import kotlinx.android.synthetic.main.item_participant_owner.view.*
import ru.rambler.libs.swipe_layout.SwipeLayout


private const val VIEW_TYPE_OWNER = 1
private const val VIEW_TYPE_EDITABLE_PARTICIPANT = 2
private const val VIEW_TYPE_PARTICIPANT = 3

class ParticipantsAdapter : BaseAdapter<User, RecyclerView.ViewHolder>() {
    var removedListener: ((Int) -> Unit?)? = null
    var editedListener: ((Int) -> Unit?)? = null
    var editable: Boolean = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    lateinit var ownerId: String

    override fun getItemViewType(position: Int) = when {
        getItem(position).id.equals(ownerId) -> VIEW_TYPE_OWNER
        editable -> VIEW_TYPE_EDITABLE_PARTICIPANT
        else -> VIEW_TYPE_PARTICIPANT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        VIEW_TYPE_OWNER -> OwnerViewHolder(inflate(parent, R.layout.item_participant_owner))
        VIEW_TYPE_EDITABLE_PARTICIPANT -> ParticipantEditableViewHolder(inflate(parent,
                R.layout.item_participant_editable))
        else -> ParticipantViewHolder(inflate(parent, R.layout.item_participant))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val user = getItem(position)
        when (getItemViewType(position)) {
            VIEW_TYPE_OWNER -> {
                val ownerViewHolder = viewHolder as OwnerViewHolder
                ownerViewHolder.fill(user)
            }
            VIEW_TYPE_EDITABLE_PARTICIPANT -> {
                val participantEditableViewHolder =
                        viewHolder as ParticipantEditableViewHolder
                participantEditableViewHolder.fill(user)
                participantEditableViewHolder.itemView.swipeLayoutEditableParticipant
                        .setOnSwipeListener(object : OnSwipeListener {
                            override fun onSwipeClampReached(swipeLayout: SwipeLayout?,
                                                             moveToRight: Boolean) {
                                if (moveToRight) {
                                    editedListener?.invoke(viewHolder.adapterPosition)
                                } else {
                                    removedListener?.invoke(viewHolder.adapterPosition)
                                }
                            }
                        })
            }
            else -> {
                val participantViewHolder = viewHolder as ParticipantViewHolder
                participantViewHolder.fill(user)
            }
        }
    }

    fun notifyUpdate(participantId: String) {
        getItems().forEachIndexed { index, user ->
            if (user.id == participantId) {
                notifyItemChanged(index)
                return@forEachIndexed
            }
        }
    }

    class ParticipantEditableViewHolder constructor(itemView: View)
        : RecyclerView.ViewHolder(itemView) {
        fun fill(user: User) {
            itemView.swipeLayoutEditableParticipant.reset()
            itemView.tvParticipantEditableFullName.text = user.fullName
            itemView.tvParticipantEditableRate.text = resources
                    .getString(R.string.euro_price_pattern, user.rate)
        }
    }

    class OwnerViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun fill(user: User) {
            itemView.tvParticipantOwnerFullName.text = user.fullName
            itemView.tvParticipantOwnerRate.text = resources
                    .getString(R.string.euro_price_pattern, user.rate)
        }
    }

    class ParticipantViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun fill(user: User) {
            itemView.tvParticipantFullName.text = user.fullName
            itemView.tvParticipantRate.text = resources
                    .getString(R.string.euro_price_pattern, user.rate)
        }
    }
}