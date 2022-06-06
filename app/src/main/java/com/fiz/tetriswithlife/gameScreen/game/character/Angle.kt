package com.fiz.tetriswithlife.gameScreen.game.character

import com.fiz.tetriswithlife.util.componentX
import com.fiz.tetriswithlife.util.componentY
import java.io.Serializable

data class Angle(val angle: Double) : Serializable {

    val direction: Character.Companion.Direction
        get() = when {
            angle.componentX == 0 && angle.componentY == 0 -> Character.Companion.Direction.Stop
            angle.componentX == 1 && angle.componentY == 0 -> Character.Companion.Direction.Right
            angle.componentX == -1 && angle.componentY == 0 -> Character.Companion.Direction.Left
            angle.componentX == 0 && angle.componentY == 1 -> Character.Companion.Direction.Down
            angle.componentX == 0 && angle.componentY == -1 -> Character.Companion.Direction.Up
            else -> throw Exception("Error: incorrect direction ${angle.componentX} ${angle.componentY}")
        }

    val isLeftUp = angle == 225.0

    val isLeftDown = angle == 135.0

    val isRightUp = angle == 315.0

    val isRightDown = angle == 45.0

    val isLeft = angle == 180.0

    val isRight = angle == 0.0 || angle == 360.0

    val isUp = angle == 270.0

    val isDown = angle == 90.0

    operator fun plus(angle: Angle): Angle {
        var newAngle = this.angle + angle.angle
        newAngle %= 360
        if (newAngle < 0)
            newAngle += 360
        return Angle(newAngle)
    }
}