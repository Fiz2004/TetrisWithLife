package com.fiz.tetriswithlife.gameScreen.game.character

import com.fiz.tetriswithlife.gameScreen.game.Coordinate
import java.io.Serializable

data class Location(var position: Coordinate) : Serializable {

    var angle: Angle = Angle(90F)

    fun addPosition(value: Double) {
        position += Coordinate(
            angle.directionX.toDouble() * value,
            angle.directionY.toDouble() * value
        )
    }
}