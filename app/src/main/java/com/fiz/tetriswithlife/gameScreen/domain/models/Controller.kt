package com.fiz.tetriswithlife.gameScreen.domain.models

private const val SecTimeOutInput = 0.08

data class Controller(
    val down: Boolean = false,
    val up: Boolean = false,
    val left: Boolean = false,
    val right: Boolean = false,
) {
    private var timeLast: Double = 0.0

    fun isCanTimeLast(deltaTime: Double): Boolean {
        if (timeLast == 0.0) {
            timeLast = SecTimeOutInput
        } else {
            timeLast -= deltaTime
            if (timeLast < 0.0)
                timeLast = 0.0
            return false
        }
        return true
    }
}