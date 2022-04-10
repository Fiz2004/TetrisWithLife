package com.fiz.tetriswithlife.game.domain.models.character

import java.io.Serializable

data class Speed(var line: Float, var rotate: Float) : Serializable {
    fun isNotRotated(): Boolean {
        return rotate == 0F
    }
}