package com.fiz.tetriswithlife.gameScreen.game.character

import com.fiz.tetriswithlife.gameScreen.game.Game
import com.fiz.tetriswithlife.gameScreen.game.Vector
import java.io.Serializable
import kotlin.math.floor

// TODO Проверить когда персонажа запирают в одной клетке, игра начинается заново до того как он задохнется

// Время без дыхания для проигрыша
const val TIMES_BREATH_LOSE = 60.0

private const val NUMBER_FRAMES_CHARACTER_MOVE = 5

data class Character(
    var location: Location,
    // TODO Сделать определение ширины и высоты жука програмным, чтобы не зависит от вида картинки
    val size: Vector = Vector(24, 24),

    val movement: Movement = Movement(),

    val breath: Breath = Breath(),

    var eat: Boolean = false,
) : Serializable {

    fun updateBreath(deltaTime: Double) {
        breath.updateBreath(deltaTime)
    }

    fun move(deltaTime: Double) {
        if (movement.isNotRotated())
            location.addPosition(movement.speed.line.toDouble())
        else
            location.angle += Angle(movement.speed.rotate)
    }

    fun isNewFrame(): Boolean {
        val deviantLine = 1.0 / 2000.0
        val deviantAngle = 1.0 / 100.0

        val isNewFrameByX =
            location.position.x % 1 < deviantLine || location.position.x % 1 > 1 - deviantLine

        val isNewFrameByY =
            location.position.y % 1 < deviantLine || location.position.y % 1 > 1 - deviantLine

        val isNewFrameByRotate =
            (location.angle.angle / 45) % 2 !in (deviantAngle..2 - deviantAngle)

        val result = isNewFrameByX && isNewFrameByY && isNewFrameByRotate

        return result
    }

    fun updateByNewFrame(game: Game) {
        eat = false

        movement.updateByNewFrame(
            location.position.posTile,
            game,
            location.angle.angle,
            isMoveStraight()
        ) { eat = true }
    }

    fun setBreath(value: Boolean) {
        this.breath.breath = value
    }

    fun getDirectionEat(): Char {
        return movement.getDirectionEat()
    }

    fun getSprite(): Vector {
        if (!eat) {
            if (movement.speed.line != 0F) {
                if (location.angle.isRight() && getFrame(location.position.x) == -1)
                    return Vector(2, 0)
                if (location.angle.isRight())
                    return Vector(getFrame(location.position.x), 1)

                if (location.angle.isLeft() && getFrame(location.position.x) == -1)
                    return Vector(6, 0)
                if (location.angle.isLeft())
                    return Vector(4 - getFrame(location.position.x), 2)

                if (location.angle.isDown() && getFrame(location.position.y) == -1)
                    return Vector(0, 0)
                if (location.angle.isDown())
                    return Vector(getFrame(location.position.y), 4)

                if (location.angle.isUp() && getFrame(location.position.y) == -1)
                    return Vector(4, 0)
                if (location.angle.isUp())
                    return Vector(getFrame(location.position.y), 3)
            }

            if (movement.speed.rotate != 0F) {
                if (location.angle.isRight())
                    return Vector(2, 0)
                if (location.angle.isRightDown())
                    return Vector(1, 0)
                if (location.angle.isDown())
                    return Vector(0, 0)
                if (location.angle.isLeftDown())
                    return Vector(7, 0)
                if (location.angle.isLeft())
                    return Vector(6, 0)
                if (location.angle.isLeftUp())
                    return Vector(5, 0)
                if (location.angle.isUp())
                    return Vector(4, 0)
                if (location.angle.isRightUp())
                    return Vector(3, 0)
            }

            return Vector(0, 0)
        }

        if (movement.speed.line != 0F) {

            if (location.angle.isRight() && getFrame(location.position.x) == -1)
                return Vector(2, 0)
            if (location.angle.isRight())
                return Vector(getFrame(location.position.x), 5)

            if (location.angle.isLeft() && getFrame(location.position.x) == -1)
                return Vector(6, 0)
            if (location.angle.isLeft())
                return Vector(4 - getFrame(location.position.x), 6)

            if (location.angle.isDown() && getFrame(location.position.y) == -1)
                return Vector(0, 0)
            if (location.angle.isDown())
                return Vector(getFrame(location.position.y), 8)

            if (location.angle.isUp() && getFrame(location.position.y) == -1)
                return Vector(4, 0)
            if (location.angle.isUp())
                return Vector(getFrame(this.location.position.y), 7)

        }

        return Vector(0, 0)
    }

    fun isEating(): Boolean {
        return eat && isMoveStraight()
    }

    fun isMoveStraight(): Boolean {
        return location.angle.directionX == movement.move.x && location.angle.directionY == movement.move.y
    }

    private fun getFrame(coordinate: Double): Int {
        val deviantLine = 1.0 / 100

        if (coordinate % 1 in (deviantLine..1 - deviantLine))
            return floor((coordinate % 1) * NUMBER_FRAMES_CHARACTER_MOVE).toInt()

        return -1
    }
}
