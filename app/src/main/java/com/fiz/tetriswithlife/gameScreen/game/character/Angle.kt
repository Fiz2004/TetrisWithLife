package com.fiz.tetriswithlife.gameScreen.domain.models.character

import java.io.Serializable
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class Angle(val angle: Float) : Serializable {
    val directionX
        get() = cos(angle * (Math.PI / 180)).roundToInt()

    val directionY
        get() = sin(angle * (Math.PI / 180)).roundToInt()

    operator fun plus(angle: Angle): Angle {
        var newAngle = this.angle + angle.angle
        newAngle %= 360
        if (newAngle < 0)
            newAngle += 360
        return Angle(newAngle)
    }

    fun isLeftUp(): Boolean {
        return angle == 225F
    }

    fun isLeftDown(): Boolean {
        return angle == 135F
    }

    fun isRightUp(): Boolean {
        return angle == 315F
    }

    fun isRightDown(): Boolean {
        return angle == 45F
    }

    fun isLeft(): Boolean {
        return angle == 180F
    }

    fun isRight(): Boolean {
        return angle == 0F || angle == 360F
    }

    fun isUp(): Boolean {
        return angle == 270F
    }

    fun isDown(): Boolean {
        return angle == 90F
    }
}