package com.meetings.android.view

import android.content.Context
import android.content.res.ColorStateList
import android.support.constraint.ConstraintLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.meetings.android.R
import com.meetings.android.utils.gone
import com.meetings.android.utils.px
import com.meetings.android.utils.visible
import kotlinx.android.synthetic.main.view_progress_button.view.*

private const val NO_DRAWABLE = -1

class ProgressButton @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr),
        CoordinatorLayout.AttachedBehavior {

    val isProgressShow: Boolean
        get() = progress.visibility == View.VISIBLE

    var clickListener: OnClickListener? = null
    var text: String? = null
        set(value) {
            field = value
            button.text = text
        }
    var icon: Int? = null
        set(value) {
            field = value
            if (value != NO_DRAWABLE) {
                button.icon = value?.let {
                    ContextCompat.getDrawable(context, it)
                }
            }
        }

    init {
        View.inflate(context, R.layout.view_progress_button, this)
        val attributeArray = context.obtainStyledAttributes(attrs,
                R.styleable.ProgressButton,
                defStyleAttr,
                0)
        try {
            text = attributeArray.getString(R.styleable.ProgressButton_progressButtonText)
            icon = attributeArray.getResourceId(R.styleable.ProgressButton_progressButtonIcon,
                    NO_DRAWABLE)
            setColor(attributeArray.getColor(R.styleable.ProgressButton_progressButtonBackground,
                    ContextCompat.getColor(context, R.color.colorAccent)))
            setSmallPadding(attributeArray.getBoolean(
                    R.styleable.ProgressButton_progressButtonSmallPadding,
                    false))
        } finally {
            attributeArray.recycle()
        }
        button.setOnClickListener {
            if (!isProgressShow) {
                clickListener?.onClick(it)
            }
        }
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<*> = MoveUpwardBehavior()

    fun setColor(color: Int) {
        setBackgroundTint(ColorStateList.valueOf(color))
    }

    fun setSmallPadding(value: Boolean) {
        val layoutParams = button.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.apply {
            val size = if (value) 4.px else 16.px
            topMargin = size
            bottomMargin = size
            marginStart = size
            marginEnd = size
            button.layoutParams = this
        }
    }

    fun showProgress() {
        progress.visible()
        button.text = null
        button.icon = null
    }

    fun dismissProgress() {
        progress.gone()
        button.text = text
        button.icon = icon?.let { ContextCompat.getDrawable(context, it) }
    }

    private fun setBackgroundTint(colorStateList: ColorStateList?) {
        button.backgroundTintList = colorStateList
    }
}