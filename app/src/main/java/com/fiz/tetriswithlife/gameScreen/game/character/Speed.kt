package com.fiz.tetriswithlife.gameScreen.game.character

import java.io.Serializable

data class Speed(var line: Double, var rotate: Double) : Serializable {

    val isRotated = rotate != 0.0

    val isMove = line != 0.0

    val isStop = line == 0.0 && rotate == 0.0

}