package com.meetings.android.utils

import android.app.Activity
import android.content.res.Resources
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.meetings.android.R

fun TextInputLayout.checkNonEmpty(): Boolean {
    return if (editText!!.text.toString().isEmpty()) {
        this@checkNonEmpty.error = "Field must not be empty"
        isErrorEnabled = true
        false
    } else {
        this@checkNonEmpty.error = null
        isErrorEnabled = false
        true
    }
}

fun TextInputLayout.etToString(): String = this.editText!!.text.toString()

fun TextInputLayout.etToDouble(): Double = this.editText!!.text.toString().toDouble()

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.hideKeyboard(): Boolean {
    try {
        return context.inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    } catch (ignored: RuntimeException) {
    }
    return false
}

fun Snackbar.style(): Snackbar {
    val textId = android.support.design.R.id.snackbar_text
    val actionId = android.support.design.R.id.snackbar_action
    view.findViewById<TextView>(textId).apply {
        typeface = ResourcesCompat.getFont(context,
                R.font.raleway)
        setTextColor(ContextCompat.getColor(context,
                android.R.color.white))
    }
    view.findViewById<TextView>(actionId).apply {
        typeface = ResourcesCompat.getFont(context,
                R.font.raleway_medium)
        setTextColor(ContextCompat.getColor(context,
                R.color.rbColorActivated))
    }
    return this
}

fun RadioGroup.checkedText(activity: Activity) =
        activity.findViewById<RadioButton>(checkedRadioButtonId)
                .text
                .toString()
                .toLowerCase()

val RecyclerView.ViewHolder.resources: Resources
    get() = itemView.context.resources