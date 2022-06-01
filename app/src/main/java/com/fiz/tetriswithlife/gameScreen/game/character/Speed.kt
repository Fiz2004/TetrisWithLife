package com.fiz.tetriswithlife.gameScreen.game.character

import java.io.Serializable

data class Speed(var line: Double, var rotate: Double) : Serializable {
    fun isRotated(): Boolean {
        return rotate != 0.0
    }

    fun isMove(): Boolean {
        return line != 0.0
    }

    fun isStop(): Boolean {
        return line == 0.0 && rotate == 0.0
    }
}