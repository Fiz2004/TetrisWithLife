package com.fiz.tetriswithlife.gameScreen.game

import java.io.Serializable
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class Vector(val x: Int, val y: Int) : Serializable {
    operator fun plus(p: Vector): Vector {
        return Vector(this.x + p.x, this.y + p.y)
    }

    operator fun times(tile: Int): Vector {
        return Vector(this.x * tile, this.y * tile)
    }

    fun equalsWith(currentAngle: Float): Boolean {
        return cos(currentAngle * (Math.PI / 180)).roundToInt() == x
                && sin(currentAngle * (Math.PI / 180)).roundToInt() == y
    }

    fun getAngleInDegrees(): Float {
        return (atan2(
            y.toDouble(),
            x.toDouble()
        ) * (180 / Math.PI)).toFloat()
    }

    fun getDirectionEat(): Char {
        if (x == -1 && y == 0)
            return 'R'

        if (x == 1 && y == 0)
            return 'L'

        if (x == 0 && y == 1)
            return 'U'

        throw Exception("Error: incorrect value function getDirectionEat $x $y")
    }
}