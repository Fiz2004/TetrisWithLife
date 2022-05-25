package com.fiz.tetriswithlife.gameScreen.game

import com.fiz.tetriswithlife.gameScreen.game.character.Character
import java.io.Serializable
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class Vector(val x: Int, val y: Int) : Serializable {

    val angleInDegrees
        get() = atan2(y.toDouble(), x.toDouble()) * (180 / Math.PI)

    operator fun plus(p: Vector): Vector {
        return Vector(this.x + p.x, this.y + p.y)
    }

    operator fun plus(p: Coordinate): Coordinate {
        return Coordinate(this.x + p.x, this.y + p.y)
    }

    operator fun plus(p: Character.Companion.Direction): Vector {
        return Vector(this.x + p.value.x, this.y + p.value.y)
    }

    operator fun minus(value: Vector): Vector {
        return Vector(this.x - value.x, this.y - value.y)
    }

    operator fun times(tile: Int): Vector {
        return Vector(this.x * tile, this.y * tile)
    }

    operator fun times(tile: Double): Vector {
        return Vector((this.x * tile).toInt(), (this.y * tile).toInt())
    }

    fun equalsWith(currentAngle: Double): Boolean {
        return cos(currentAngle * (Math.PI / 180)).roundToInt() == x
                && sin(currentAngle * (Math.PI / 180)).roundToInt() == y
    }
}