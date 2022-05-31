package com.fiz.tetriswithlife.gameScreen.ui.models

import android.graphics.Rect
import android.graphics.RectF

data class CurrentFigureUi(
    val value: Int,
    val src: Rect,
    val dst: RectF
)