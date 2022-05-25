package com.fiz.tetriswithlife.gameScreen.game.character

import com.fiz.tetriswithlife.gameScreen.game.Coordinate
import com.fiz.tetriswithlife.gameScreen.game.Grid
import com.fiz.tetriswithlife.gameScreen.game.Vector
import java.io.Serializable

// Время без дыхания для проигрыша
const val TIMES_BREATH_LOSE = 60.0

private const val BASE_SPEED_FOR_SECOND = 1.0 / 1000.0

private const val BASE_SPEED_ROTATE_FOR_SECOND = 45.0

class Character private constructor(startPosition: Coordinate) : Serializable {

    val breath: Breath = Breath()

    var position: Coordinate = startPosition
        private set

    var angle: Angle = Angle(90.0)
        private set

    var eat: Boolean = false
        private set

    var speed: Speed = Speed(0.0, 0.0)
        private set

    var move: Direction = Direction.Stop
        private set

    var lastDirection: Direction = Direction.Right
        private set

    var moves: MutableList<Direction> = mutableListOf()
        private set


    val positionTile
        get() = position.posTile

    private fun addPosition(value: Double) {
        position += Coordinate(
            angle.directionX.toDouble() * value,
            angle.directionY.toDouble() * value
        )
    }

    fun isNewFrame(): Boolean {
        val deviantLine = 1.0 / 2000.0
        val deviantAngle = 1.0 / 100.0

        val isNewFrameByX =
            position.x % 1 < deviantLine || position.x % 1 > 1 - deviantLine

        val isNewFrameByY =
            position.y % 1 < deviantLine || position.y % 1 > 1 - deviantLine

        val isNewFrameByRotate =
            (angle.angle / 45) % 2 !in (deviantAngle..2 - deviantAngle)

        return isNewFrameByX && isNewFrameByY && isNewFrameByRotate
    }

    private fun getCurrentMove() =
        if (move == moves.first()) {
            if (angle.direction == move)
                moves.removeFirst()
            else
                move
        } else {
            moves.first()
        }

    fun setBreath(value: Boolean) {
        this.breath.breath = value
    }

    fun isEating(): Boolean {
        return eat && angle.directionX == move.value.x && angle.directionY == move.value.y
    }

    fun move(deltaTime: Double) {
        if (speed.isMove())
            addPosition(speed.line)

        if (speed.isRotated())
            angle += Angle(speed.rotate)
    }

    fun newFrame(newMoves: List<Direction>) {
        moves = newMoves.toMutableList()

        move = getCurrentMove()
    }

    fun setSpeed() {
        speed = getSpeed(
            angle.angle,
            move
        )
    }

    private fun getSpeed(currentAngle: Double, needVector: Direction): Speed {
        val tempAngle = needVector.value.angleInDegrees

        var signAtClockwise = 1
        if ((currentAngle - tempAngle) in (0.0..180.0))
            signAtClockwise = -1

        if (needVector.value.equalsWith(currentAngle))
            return Speed(BASE_SPEED_FOR_SECOND, 0.0)

        if (currentAngle == tempAngle)
            return Speed(0.0, 0.0)

        return Speed(0.0, signAtClockwise * BASE_SPEED_ROTATE_FOR_SECOND)
    }

    fun setEat(value: Boolean) {
        eat = value
    }

    fun setLastDirection(value: Direction) {
        lastDirection = value
    }


    companion object {
        fun create(
            grid: Grid, coordinate: Coordinate = Coordinate(
                grid.space[grid.space.lastIndex].indices.shuffled().first().toDouble(),
                (grid.space.lastIndex).toDouble()
            )
        ): Character {
            return Character(startPosition = coordinate)
        }

        enum class Direction(val value: Vector) {
            Left(Vector(-1, 0)), Right(Vector(1, 0)), Up(Vector(0, -1)), Down(Vector(0, 1)), Stop(
                Vector(0, 0)
            )
        }
    }
}
