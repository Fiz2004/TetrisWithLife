package com.fiz.tetriswithlife.gameScreen.domain.models.character

import java.io.Serializable

class Breath : Serializable {
    var secondsSupplyForBreath = TIMES_BREATH_LOSE

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