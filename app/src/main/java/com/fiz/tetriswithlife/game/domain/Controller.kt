package com.fiz.tetriswithlife.game.domain

data class Controller(
    var down: Boolean = false,
    var up: Boolean = false,
    var left: Boolean = false,
    var right: Boolean = false,
    var timeLast: Double = 0.0
) {
    fun actionCancel(){
        down = false
        up = false
        left = false
        right = false
    }

    fun actionDown(){
        down = true
        up = false
        left = false
        right = false
    }

    fun actionUp(){
        down = false
        up = true
        left = false
        right = false
    }

    fun actionLeft(){
        down = false
        up = false
        left = true
        right = false
    }

    fun actionRight(){
        down = false
        up = false
        left = false
        right = true
    }

    fun isCannotTimeLast(deltaTime:Double):Boolean{
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