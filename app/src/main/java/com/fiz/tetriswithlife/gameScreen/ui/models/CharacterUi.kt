package com.fiz.tetriswithlife.gameScreen.ui.models

import android.graphics.Rect

data class CharacterUi(
    val src: Rect,
    val dst: Rect,
    val breath: Boolean,
    val secondsSupplyForBreath: Double
)