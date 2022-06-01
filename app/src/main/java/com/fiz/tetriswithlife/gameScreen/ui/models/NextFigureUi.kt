package com.fiz.tetriswithlife.gameScreen.ui.models

import android.graphics.Rect

data class NextFigureUi(
    val value: Int,
    val src: Rect,
    val dst: Rect
)