package com.fiz.tetriswithlife.gameScreen.game

import com.fiz.tetriswithlife.util.componentX
import com.fiz.tetriswithlife.util.componentY
import java.io.Serializable
import kotlin.math.atan2

data class Vector(val x: Int, val y: Int) : Serializable {

    val angleInDegrees
        get() = atan2(y.toDouble(), x.toDouble()) * (180.0 / Math.PI)

    val toCoordinate
        get() = Coordinate(x.toDouble(), y.toDouble())

    fun equalsWith(currentAngle: Double): Boolean {
        return currentAngle.componentX == x
                && currentAngle.componentY == y
    }

    operator fun plus(value: Vector): Vector {
        return Vector(this.x + value.x, this.y + value.y)
    }

    operator fun plus(value: Coordinate): Coordinate {
        return Coordinate(this.x + value.x, this.y + value.y)
    }

    operator fun minus(value: Vector): Vector {
        return Vector(this.x - value.x, this.y - value.y)
    }

    operator fun times(value: Int): Vector {
        return Vector(this.x * value, this.y * value)
    }

    operator fun times(value: Double): Coordinate {
        return Coordinate(this.x * value, this.y * value)
    }
}