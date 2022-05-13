package com.fiz.tetriswithlife.game.domain.character

import com.fiz.tetriswithlife.game.domain.grid.Coordinate
import com.fiz.tetriswithlife.game.domain.grid.Grid
import com.fiz.tetriswithlife.game.domain.grid.Point
import kotlin.math.*

//TODO Проверить когда персонажа запирают в одной клетке, игра начинается заново до того как он
// задохнется
private const val NUMBER_FRAMES_CHARACTER_MOVE = 5
private const val CHARACTER_SPEED_LINE = 30.0
private const val CHARACTER_SPEED_ROTATE = 45

data class Speed(var line: Float, var rotate: Float)

open class Character(grid: Grid) {
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
    private var moves: MutableList<Point> = mutableListOf()
    private var lastDirection = 1
    private val posTileX: Int
        get() = position.x.roundToInt()
    private val posTileY: Int
        get() = position.y.roundToInt()
    private val directionX
        get() = cos(angle * (Math.PI / 180)).roundToInt()
    private val directionY
        get() = sin(angle * (Math.PI / 180)).roundToInt()


    open fun update(grid: Grid): String {
        changePosition()

        if (isNewFrame())
            return updateNewFrame(grid)

        return "true"
    }

    private fun changePosition() {
        if (speed.rotate == 0F) {
            position += Coordinate(
                speed.line * directionX.toDouble(),
                speed.line * directionY.toDouble()
            )
        } else {
            angle += speed.rotate
            angle %= 360
            if (angle < 0)
                angle += 360
        }
    }

    fun isNewFrame(): Boolean {
        return (position.x % 1 < (1 / CHARACTER_SPEED_LINE)
                || position.x % 1 > (1 - (1 / CHARACTER_SPEED_LINE))
                ) && (position.y % 1 < (1 / CHARACTER_SPEED_LINE)
                || position.y % 1 > (1 - (1 / CHARACTER_SPEED_LINE))
                ) && ((angle / CHARACTER_SPEED_ROTATE) % 2 < 0.01
                || (angle / CHARACTER_SPEED_ROTATE) % 2 > 1.99)
    }

    private fun updateNewFrame(grid: Grid): String {
        moves = getDirection(grid)

        if (move.x == moves[0].x && move.y == moves[0].y) {
            if (isMoveStraight())
                move = moves.removeAt(0)
        } else {
            move = moves.first()
        }

        speed = getSpeedAngle()

        return "true"
    }

    fun isMoveStraight(): Boolean {
        return directionX == move.x && directionY == move.y
    }

    private fun getSpeedAngle(): Speed {
        val tempAngle = atan2(move.y.toDouble(), move.x.toDouble()) * (180 / Math.PI)
        var sign = 1
        if ((angle - tempAngle) > 0 && (angle - tempAngle) < 180)
            sign = -1

        if (cos(angle * (Math.PI / 180)).roundToInt() == move.x
            && sin(angle * (Math.PI / 180)).roundToInt() == move.y
        )
            return Speed((1 / 10.0).toFloat(), 0F)

        if (angle == tempAngle.toFloat())
            return Speed(0F, 0F)

        return Speed(0F, (sign * CHARACTER_SPEED_ROTATE).toFloat())
    }

    private fun getDirection(grid: Grid): MutableList<Point> {
        // Проверяем свободен ли выбранный путь при фиксации фигуры
        if (deleteRow == 1
            && moves == isCanMove(arrayOf(moves.toTypedArray()), grid)
        )
            deleteRow = 0

        if (moves.isEmpty() || deleteRow == 1)
            return getNewDirection(grid)

        return moves
    }

    private fun getNewDirection(grid: Grid): MutableList<Point> {
        val direction = DIRECTION()
        deleteRow = 0
        // Если двигаемся вправо
        if (((speed.line == 0F && speed.rotate == 0F) && move.x == 1) || move.x == 1) {
            lastDirection = 1
            return isCanMove(
                arrayOf(*direction.RIGHT_DOWN, *direction.RIGHT, *direction.LEFT), grid
            ).toMutableList()
        }
        // Если двигаемся влево
        if (((speed.line == 0F && speed.rotate == 0F) && move.x == -1) || move.x == -1) {
            lastDirection = -1
            return isCanMove(arrayOf(*direction.LEFT_DOWN, *direction.LEFT, *direction.RIGHT), grid)
                .toMutableList()
        }

        if (lastDirection == -1)
            return isCanMove(
                arrayOf(direction._0D, *direction.LEFT, *direction.RIGHT),
                grid
            ).toMutableList()

        return isCanMove(
            arrayOf(direction._0D, *direction.RIGHT, *direction.LEFT),
            grid
        )
            .toMutableList()
    }

    open fun isCanMove(arrayDirections: Array<Array<Point>>, grid: Grid): Array<Point> {
        for (directions in arrayDirections)
            if (isCanDirections(directions, grid))
                return directions

        return arrayOf(Point(0, 0))
    }

    private fun isCanDirections(directions: Array<Point>, grid: Grid): Boolean {
        var addPoint = Point(0, 0)
        for (direction in directions) {
            addPoint += direction
            val point = posTile + addPoint
            if (grid.isOutside(point) || grid.isNotFree(point))
                return false
        }

        return true
    }

    // Исходя из данных определяет спрайт для рисования
    open fun getSprite(): Point {
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
}

data class DIRECTION(
    val L: Point = Point(-1, 0),
    val R: Point = Point(1, 0),
    val D: Point = Point(0, 1),
    val U: Point = Point(0, -1),
    val _0: Point = Point(0, 0),
    val _0D: Array<Point> = arrayOf(D),
    val RD: Array<Point> = arrayOf(D, R),
    val LD: Array<Point> = arrayOf(D, L),
    val R0: Array<Point> = arrayOf(R),
    val L0: Array<Point> = arrayOf(L),
    val RU: Array<Point> = arrayOf(U, R),
    val LU: Array<Point> = arrayOf(U, L),
    val RUU: Array<Point> = arrayOf(U, U, R),
    val LUU: Array<Point> = arrayOf(U, U, L),
    val LEFT_DOWN: Array<Array<Point>> = arrayOf(_0D, LD, RD),
    val RIGHT_DOWN: Array<Array<Point>> = arrayOf(_0D, RD, LD),
    val LEFT: Array<Array<Point>> = arrayOf(L0, LU, LUU),
    val RIGHT: Array<Array<Point>> = arrayOf(R0, RU, RUU),
)

fun getFrame(coordinate: Double): Int {
    if (coordinate % 1 > 0.01 && coordinate % 1 < 0.99)
        return floor((coordinate % 1) * NUMBER_FRAMES_CHARACTER_MOVE).toInt()

    return -1
}
