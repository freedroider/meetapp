package com.meetings.android.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.annotation.ColorRes
import android.support.annotation.FloatRange
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.TypedValue

class DividerDecoration(context: Context, @ColorRes colorRes: Int,
                        @FloatRange(from = 0.0) paddingDp: Float = 16.0f,
                        @FloatRange(from = 0.0, fromInclusive = false) heightDp: Float = 1.0f)
    : RecyclerView.ItemDecoration() {
    private val paint: Paint = Paint()
    private val paddingPx: Int

    init {
        paint.color = ContextCompat.getColor(context, colorRes)
        paddingPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                paddingDp, context.resources.displayMetrics).toInt()
        val thickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                heightDp, context.resources.displayMetrics)
        paint.strokeWidth = thickness
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val offset = (paint.strokeWidth / 2).toInt()
        for (index in 0 until parent.childCount) {
            val view = parent.getChildAt(index)
            val params = view.layoutParams as RecyclerView.LayoutParams
            val position = params.viewAdapterPosition
            if (position < state.itemCount) {
                canvas.drawLine((view.left + paddingPx).toFloat(),
                        (view.bottom + offset).toFloat(),
                        (view.right - paddingPx).toFloat(),
                        (view.bottom + offset).toFloat(),
                        paint)
            }
        }
    }
}