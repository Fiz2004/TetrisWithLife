package com.fiz.tetriswithlife.gameScreen.ui.models

import android.graphics.Rect
import android.graphics.RectF

data class NextFigureUi(
    val value: Int,
    val src: Rect,
    val dst: RectF
)