package com.fiz.tetriswithlife.game.ui

data class UIState(
    val scores: String = "000000",
    val record: String = "000000",
    val pauseResumeButton: Int = 0,
    val infoBreathTextViewVisibility: Boolean = false,
    val textForBreathTextView: String = "0",
    val colorForBreathTextView: Int = 0
)