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

fun Activity.isKeyboardOpenCallback() {
    //TODO rootView.getWindowVisibleDisplayFrame(Rect())
    var isKeyboardShowing = false

// ContentView is the root view of the layout of this activity/fragment
    val contentView = window.decorView.rootView

    contentView.viewTreeObserver.addOnGlobalLayoutListener {
        fun onGlobalLayout() {

            val r = Rect()
            contentView.getWindowVisibleDisplayFrame(r)
            val screenHeight: Int = contentView.rootView.height

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            val keypadHeight: Int = screenHeight - r.bottom;

            Log.d("M_Activity", "keypadHeight = $keypadHeight")

            if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                // keyboard is opened
                if (!isKeyboardShowing) {
                    isKeyboardShowing = true
                    onKeyboardVisibilityChanged(true)
                }
            } else {
                // keyboard is closed
                if (isKeyboardShowing) {
                    isKeyboardShowing = false
                    onKeyboardVisibilityChanged(false)
                }
            }
        }
    }

}

fun onKeyboardVisibilityChanged(opened: Boolean) {
    println("keyboard $opened")
}