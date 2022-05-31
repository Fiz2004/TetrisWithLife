package com.fiz.tetriswithlife.gameScreen.ui.models

import android.graphics.Rect
import android.graphics.RectF

data class CharacterUi(
    val src: Rect,
    val dst: RectF,
    val breath: Boolean,
    val secondsSupplyForBreath: Double
)