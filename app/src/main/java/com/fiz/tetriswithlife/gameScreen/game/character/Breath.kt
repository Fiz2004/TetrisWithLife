package com.fiz.tetriswithlife.gameScreen.game.character

import java.io.Serializable

data class Breath(var secondsSupplyForBreath: Double = TIMES_BREATH_LOSE) : Serializable {

    var breath = true
        set(value) {
            if (breath && !value)
                secondsSupplyForBreath = TIMES_BREATH_LOSE

            field = value
        }

    fun updateBreath(deltaTime: Double) {
        if (!breath)
            secondsSupplyForBreath -= deltaTime
    }
}