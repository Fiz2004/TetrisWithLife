package com.fiz.tetriswithlife.util

import android.view.View

fun View.setVisible(visibility: Boolean) {
    this.visibility = if (visibility) View.VISIBLE else View.INVISIBLE
}