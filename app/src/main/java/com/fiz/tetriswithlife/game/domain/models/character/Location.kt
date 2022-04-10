package com.fiz.tetriswithlife.game.domain.models.character

import com.fiz.tetriswithlife.game.domain.models.Coordinate
import java.io.Serializable

class Location(var position: Coordinate) : Serializable {
    var angle: Angle = Angle(90F)

    fun addPosition(value: Double) {
        position += Coordinate(
            angle.directionX.toDouble() * value,
            angle.directionY.toDouble() * value
        )
    }
}