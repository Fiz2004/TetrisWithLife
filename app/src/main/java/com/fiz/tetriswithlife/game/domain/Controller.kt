package com.fiz.tetriswithlife.game.domain

data class Controller(
    val down: Boolean = false,
    val up: Boolean = false,
    val left: Boolean = false,
    val right: Boolean = false,
) {
    private var timeLast: Double = 0.0

    fun isCannotTimeLast(deltaTime: Double): Boolean {
        if (timeLast == 0.0) {
            timeLast = 0.08
        } else {
            timeLast -= deltaTime
            if (timeLast < 0.0)
                timeLast = 0.0
            return true
        }
        return false
    }
}