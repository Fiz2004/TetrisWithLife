package com.fiz.tetriswithlife.gameScreen.game.character

import java.io.Serializable
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class Angle(val angle: Double) : Serializable {

    val direction: Character.Companion.Direction
        get() = when {
            directionX == 0 && directionY == 0 -> Character.Companion.Direction.Stop
            directionX == 1 && directionY == 0 -> Character.Companion.Direction.Right
            directionX == -1 && directionY == 0 -> Character.Companion.Direction.Left
            directionX == 0 && directionY == 1 -> Character.Companion.Direction.Down
            directionX == 0 && directionY == -1 -> Character.Companion.Direction.Up
            else -> throw Exception("Error: incorrect direction $directionX $directionY")
        }

    val directionX
        get() = cos(angle * (Math.PI / 180)).roundToInt()

    val directionY
        get() = sin(angle * (Math.PI / 180)).roundToInt()

    fun isLeftUp() = angle == 225.0

    fun isLeftDown() = angle == 135.0

    fun isRightUp() = angle == 315.0

    fun isRightDown() = angle == 45.0

    fun isLeft() = angle == 180.0

    fun isRight() = angle == 0.0 || angle == 360.0

    fun isUp() = angle == 270.0

    fun isDown() = angle == 90.0

    operator fun plus(angle: Angle): Angle {
        var newAngle = this.angle + angle.angle
        newAngle %= 360
        if (newAngle < 0)
            newAngle += 360
        return Angle(newAngle)
    }
}