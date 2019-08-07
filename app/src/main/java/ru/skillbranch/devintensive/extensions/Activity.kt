package ru.skillbranch.devintensive.extensions

import android.app.Activity
import android.graphics.Rect
import android.util.Log
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard() = (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
    .hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)

fun Activity.isKeyboardOpen(): Boolean {
    val contentView = window.decorView.rootView
    val r = Rect()

    contentView.getWindowVisibleDisplayFrame(r)
    val screenHeight: Int = contentView.rootView.height
    val keypadHeight: Int = screenHeight - r.bottom

    Log.d("M_Activity", "keypadHeight = $keypadHeight")
    return keypadHeight > screenHeight * 0.15
}

fun Activity.isKeyboardClosed() = !this.isKeyboardOpen()