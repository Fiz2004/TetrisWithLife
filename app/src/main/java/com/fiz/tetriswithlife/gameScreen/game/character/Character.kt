package com.fiz.tetriswithlife.gameScreen.game.character

import com.fiz.tetriswithlife.gameScreen.game.Coordinate
import com.fiz.tetriswithlife.gameScreen.game.Grid
import com.fiz.tetriswithlife.gameScreen.game.PROBABILITY_EAT_PERCENT
import com.fiz.tetriswithlife.gameScreen.game.Vector
import java.io.Serializable
import kotlin.random.Random

// Время без дыхания для проигрыша
const val TIMES_BREATH_LOSE = 60.0

private const val BASE_SPEED_FOR_SECOND = 1.0

private const val BASE_SPEED_ROTATE_FOR_SECOND = 45.0

class Character private constructor(startPosition: Coordinate) : Serializable {

    var position = startPosition
        private set

    var angle = Angle(90.0)
        private set

    var eat = false
        private set

    var speed = Speed(0.0, 0.0)
        private set

    var move = Direction.Stop
        private set

    var isEatFinish = false
        private set

    val positionTile
        get() = position.posTile

    val breath: Breath = Breath()

    val isCharacterNoBreath
        get() = breath.secondsSupplyForBreath <= 0

    private var path: MutableList<Direction> = mutableListOf()

    private var lastDirection: Direction = Direction.Right

    private var isDeleteRow: Boolean = false

    var isLastNewFrame = true

    private val isNewFrame: Boolean
        get() {
            val deviantLine = 1.0 / 60.0 / 2.0
            val deviantAngle = 1.0 / 100.0
            val isNewFrameByX = (position.x % 1) !in deviantLine..(1 - deviantLine)
            val isNewFrameByY = (position.y % 1) !in deviantLine..(1 - deviantLine)
            val isNewFrameByRotate = (angle.angle / 45) % 2 !in (deviantAngle..2 - deviantAngle)
            val result = isNewFrameByX && isNewFrameByY && isNewFrameByRotate
            isLastNewFrame = result
            return result
        }

    private val currentMove: Direction
        get() = if (move == path.first()) {
            if (angle.direction == move) path.removeFirst()
            else move
        } else {
            path.first()
        }

    fun update(
        deltaTime: Double, isOutside: (Vector) -> Boolean, isNotFree: (Vector) -> Boolean
    ) {
        breath.updateBreath(deltaTime)
        move(deltaTime)

        val isEatenBefore = eat
        if (isNewFrame) {

            newFrame(getPath(isOutside, isNotFree))

            if (isEatenBefore && speed.isMove) isEatFinish = true

            speed = getSpeed(
                angle.angle, move
            )
        }
    }

    private fun move(deltaTime: Double) {
        if (speed.isMove) {
            val step = if (isLastNewFrame) {
                angle.direction.value * speed.line * (1.0 / 60.0)
            } else {
                angle.direction.value * speed.line * deltaTime
            }
            position += step
        }

        if (speed.isRotated) {
            angle += Angle(speed.rotate)
        }
    }

    fun setBreath(value: Boolean) {
        this.breath.breath = value
    }

    fun isEating(): Boolean {
        // Если angle не соотвествует основным направлениям, то делаем ошибку и возвращает что не едим
        return try {
            eat && angle.direction == move
        } catch (e: java.lang.Exception) {
            false
        }
    }

    private fun newFrame(newPath: List<Direction>) {
        path = newPath.toMutableList()

        move = currentMove
    }

    private fun getSpeed(currentAngle: Double, needVector: Direction): Speed {
        val needAngle = needVector.value.angleInDegrees

        var signAtClockwise = 1
        if ((currentAngle - needAngle) in (0.0..180.0)) {
            signAtClockwise = -1
        }

        if (needVector.value.equalsWith(currentAngle)) {
            return Speed(BASE_SPEED_FOR_SECOND, 0.0)
        }

        if (currentAngle == needAngle) {
            return Speed(0.0, 0.0)
        }

        return Speed(0.0, signAtClockwise * BASE_SPEED_ROTATE_FOR_SECOND)
    }

    private fun getPath(
        isOutside: (Vector) -> Boolean, isNotFree: (Vector) -> Boolean
    ): List<Direction> {
        val isPathFree = if (isDeleteRow) {
            path == isCanMove(listOf(path), isOutside, isNotFree)
        } else {
            true
        }

        if (path.isEmpty() || path[0] == Direction.Stop || !isPathFree) {
            return getNewPath(isOutside, isNotFree)
        }

        return path
    }


    private fun getNewPath(
        isOutside: (Vector) -> Boolean, isNotFree: (Vector) -> Boolean
    ): List<Direction> {

        val presetDirection = PresetDirection()
        isDeleteRow = false

        val allPaths = when {
            speed.isStop || move == Direction.Right -> {
                lastDirection = Direction.Right
                listOf(presetDirection.RIGHT_DOWN + presetDirection.RIGHT + presetDirection.LEFT).flatten()

            }
            move == Direction.Left -> {
                lastDirection = Direction.Left
                listOf(presetDirection.LEFT_DOWN + presetDirection.LEFT + presetDirection.RIGHT).flatten()
            }

            lastDirection == Direction.Left -> listOf(presetDirection._0D) + presetDirection.LEFT + presetDirection.RIGHT


            else -> listOf(presetDirection._0D) + presetDirection.RIGHT + presetDirection.LEFT
        }

        return isCanMove(allPaths, isOutside, isNotFree)
    }

    private fun isCanMove(
        allPaths: List<List<Direction>>,
        isOutside: (Vector) -> Boolean,
        isNotFree: (Vector) -> Boolean
    ): List<Direction> {
        for (paths in allPaths) if (isCanDirectionsAndSetCharacterEat(
                paths, isOutside = isOutside, isNotFree = isNotFree
            )
        ) return paths
        return listOf(Direction.Stop)
    }

    fun isCanDirectionsAndSetCharacterEat(
        paths: List<Direction>,
        isCanEat: Boolean = Random.nextInt(100) < PROBABILITY_EAT_PERCENT,
        isOutside: (Vector) -> Boolean,
        isNotFree: (Vector) -> Boolean
    ): Boolean {
        var result = Vector(0, 0)
        var currentVector = Vector(0, 0)

        paths.forEach { direction ->
            currentVector += direction.value
            val checkingPosition = position.posTile + currentVector

            if (isOutside(checkingPosition)) return false

            result += direction.value

            if (isNotFree(checkingPosition)) {
                val isCanEatHorizontally = currentVector.y == 0 && isCanEat
                if (isCanEatHorizontally) {
                    eat = true
                    return true
                }
                return false
            }
        }
        eat = false
        return true
    }

    fun eatenFinish() {
        isEatFinish = false
    }

    fun onDeleteRow() {
        isDeleteRow = true
    }

    companion object {
        fun create(
            grid: Grid, coordinate: Coordinate = Coordinate(
                grid.space[grid.space.lastIndex].indices.shuffled().first()
                    .toDouble(),
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
