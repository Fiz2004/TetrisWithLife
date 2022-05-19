package com.fiz.tetriswithlife.game.domain.models.character

import com.fiz.tetriswithlife.game.domain.models.TIMES_BREATH_LOSE
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