package com.fiz.tetriswithlife.game.domain.character

import com.fiz.tetriswithlife.game.domain.models.Coordinate
import com.fiz.tetriswithlife.game.domain.models.Grid
import com.fiz.tetriswithlife.game.domain.models.Point
import java.io.Serializable
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin

//TODO Проверить когда персонажа запирают в одной клетке, игра начинается заново до того как он
// задохнется

// Время без дыхания для проигрыша
const val TIMES_BREATH_LOSE = 60.0

private const val NUMBER_FRAMES_CHARACTER_MOVE = 5
private const val CHARACTER_SPEED_LINE = 30.0

data class Speed(var line: Float, var rotate: Float) : Serializable

class Character(grid: Grid) : Serializable {
    // !Сделать определение ширины и высоты жука програмным, чтобы не зависит от вида картинки
    val width = 24
    val height = 24
    var position = Coordinate(
        (0 until grid.width).shuffled().first().toDouble(), (grid.height - 1)
            .toDouble()
    )
    var speed = Speed(0F, 0F)
    var angle = 90F
    var move: Point = Point(0, 0)
    var deleteRow = 0
    val posTile
        get() = Point(posTileX, posTileY)
    var moves: MutableList<Point> = mutableListOf()
    var lastDirection = 1
    private val posTileX: Int
        get() = position.x.roundToInt()
    private val posTileY: Int
        get() = position.y.roundToInt()
    val directionX
        get() = cos(angle * (Math.PI / 180)).roundToInt()
    val directionY
        get() = sin(angle * (Math.PI / 180)).roundToInt()

    var timeBreath = TIMES_BREATH_LOSE
    var breath = true
    var eat = 0

    private var tempGrid: MutableList<MutableList<Int>> = MutableList(grid.height) {
        MutableList(grid.width) {
            0
        }
    }

    private fun refreshTempGrid(grid: Grid) {
        grid.space.forEachIndexed { indexY, elements ->
            elements.forEachIndexed { indexX, element ->
                tempGrid[indexY][indexX] = element.block
            }
        }
    }

    fun isBreath(grid: Grid): Boolean {
        val temp = breath

        refreshTempGrid(grid)

        breath = findWay(posTile, grid)

        if (temp && !breath)
            timeBreath = TIMES_BREATH_LOSE

        return breath
    }

    private fun isInside(p: Point): Boolean {
        return p.y in tempGrid.indices && p.x in tempGrid[p.y].indices
    }

    private fun isFree(p: Point): Boolean {
        return tempGrid[p.y][p.x] == 0
    }

    private fun findWay(tile: Point, grid: Grid): Boolean {
        if (tile.y == 0)
            return true

        tempGrid[tile.y][tile.x] = 1

        for (shiftPoint in listOf(Point(0, -1), Point(1, 0), Point(-1, 0), Point(0, 1))) {
            val nextElement = tile + shiftPoint
            if (isInside(nextElement) && isFree(nextElement)
                && findWay(nextElement, grid)
            )
                return true
        }
        return false
    }

    fun getDirectionEat(): Char {
        if (move.x == -1 && move.y == 0)
            return 'R'

        if (move.x == 1 && move.y == 0)
            return 'L'

        if (move.x == 0 && move.y == 1)
            return 'U'

        throw Exception("Error: incorrect value function getDirectionEat $move.x $move.y")
    }

    fun getSprite(): Point {
        if (eat == 0) {
            if (angle == 0F && speed.line != 0F && getFrame(position.x) == -1)
                return Point(2, 0)
            if (angle == 0F && speed.line != 0F)
                return Point(getFrame(position.x), 1)

            if (angle == 180F && speed.line != 0F && getFrame(position.x) == -1)
                return Point(6, 0)
            if (angle == 180F && speed.line != 0F)
                return Point(4 - getFrame(position.x), 2)

            if (angle == 90F && speed.line != 0F && getFrame(position.y) == -1)
                return Point(0, 0)
            if (angle == 90F && speed.line != 0F)
                return Point(getFrame(position.y), 4)

            if (angle == 270F && speed.line != 0F && getFrame(position.y) == -1)
                return Point(4, 0)
            if (angle == 270F && speed.line != 0F)
                return Point(getFrame(position.y), 3)

            if (speed.rotate != 0F && angle == 0F)
                return Point(2, 0)
            if (speed.rotate != 0F && angle == 45F)
                return Point(1, 0)
            if (speed.rotate != 0F && angle == 90F)
                return Point(0, 0)
            if (speed.rotate != 0F && angle == 135F)
                return Point(7, 0)
            if (speed.rotate != 0F && angle == 180F)
                return Point(6, 0)
            if (speed.rotate != 0F && angle == 225F)
                return Point(5, 0)
            if (speed.rotate != 0F && angle == 270F)
                return Point(4, 0)
            if (speed.rotate != 0F && angle == 315F)
                return Point(3, 0)

            return Point(0, 0)
        }

        if (angle == 0F && speed.line != 0F && getFrame(position.x) == -1)
            return Point(2, 0)
        if (angle == 0F && speed.line != 0F)
            return Point(getFrame(position.x), 5)

        if (angle == 180F && speed.line != 0F && getFrame(position.x) == -1)
            return Point(6, 0)
        if (angle == 180F && speed.line != 0F)
            return Point(4 - getFrame(position.x), 6)

        if (angle == 90F && speed.line != 0F && getFrame(position.y) == -1)
            return Point(0, 0)
        if (angle == 90F && speed.line != 0F)
            return Point(getFrame(position.y), 8)

        if (angle == 270F && speed.line != 0F && getFrame(position.y) == -1)
            return Point(4, 0)
        if (angle == 270F && speed.line != 0F)
            return Point(getFrame(this.position.y), 7)

        return Point(0, 0)
    }

    fun isMoveStraight(): Boolean {
        return directionX == move.x && directionY == move.y
    }

}

enum class StatusCharacter {
    Nothing, Eat, EatFinish
}

data class DIRECTION(
    val L: Point = Point(-1, 0),
    val R: Point = Point(1, 0),
    val D: Point = Point(0, 1),
    val U: Point = Point(0, -1),
    val _0: Point = Point(0, 0),
    val _0D: List<Point> = listOf(D),
    val RD: List<Point> = listOf(D, R),
    val LD: List<Point> = listOf(D, L),
    val R0: List<Point> = listOf(R),
    val L0: List<Point> = listOf(L),
    val RU: List<Point> = listOf(U, R),
    val LU: List<Point> = listOf(U, L),
    val RUU: List<Point> = listOf(U, U, R),
    val LUU: List<Point> = listOf(U, U, L),
    val LEFT_DOWN: List<List<Point>> = listOf(_0D, LD, RD),
    val RIGHT_DOWN: List<List<Point>> = listOf(_0D, RD, LD),
    val LEFT: List<List<Point>> = listOf(L0, LU, LUU),
    val RIGHT: List<List<Point>> = listOf(R0, RU, RUU),
)

fun getFrame(coordinate: Double): Int {
    if (coordinate % 1 > 0.01 && coordinate % 1 < 0.99)
        return floor((coordinate % 1) * NUMBER_FRAMES_CHARACTER_MOVE).toInt()

    return -1
}
