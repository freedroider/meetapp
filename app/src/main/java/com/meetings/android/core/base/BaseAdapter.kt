package com.meetings.android.core.base


import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.rambler.libs.swipe_layout.SwipeLayout

abstract class BaseAdapter<M, VH : RecyclerView.ViewHolder>
constructor(private val items: MutableList<M> = mutableListOf()) : RecyclerView.Adapter<VH>() {
    protected fun inflate(parent: ViewGroup, @LayoutRes layoutRes: Int): View {
        return LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
    }

    fun add(item: M) {
        this.items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun add(items: List<M>) {
        val insertPosition = this.items.size
        this.items.addAll(items)
        notifyItemRangeInserted(insertPosition, items.size)
    }

    fun replace(position: Int, item: M) {
        this.items[position] = item
        notifyItemInserted(position)
    }

    fun replace(items: List<M>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return this.items.size
    }

    fun getItem(position: Int): M {
        return this.items[position]
    }

    fun getItems(): List<M> {
        return this.items
    }

    fun clear() {
        this.items.clear()
        notifyDataSetChanged()
    }

    fun remove(position: Int): M {
        val removedElement = items.removeAt(position)
        notifyItemRemoved(position)
        return removedElement
    }

    interface OnSwipeListener : SwipeLayout.OnSwipeListener {
        override fun onRightStickyEdge(swipeLayout: SwipeLayout?, moveToRight: Boolean) {
        }

        override fun onBeginSwipe(swipeLayout: SwipeLayout?, moveToRight: Boolean) {
        }

        override fun onLeftStickyEdge(swipeLayout: SwipeLayout?, moveToRight: Boolean) {
        }
    }
}
