package ru.skillbranch.devintensive.extensions

import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.google.android.material.snackbar.Snackbar

fun Snackbar.setBackgroundDrawable(@DrawableRes drawable: Int) = apply { view.setBackgroundResource(drawable) }

fun Snackbar.setTextColor(@ColorInt color: Int) = apply {
    val textView: TextView = view.findViewById(com.google.android.material.R.id.snackbar_text)
    textView.setTextColor(color)
}