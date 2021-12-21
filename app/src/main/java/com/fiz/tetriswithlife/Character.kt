package com.fiz.tetriswithlife

private const val NUMBER_FRAMES_BEEATLE_MOVE = 5
private const val CHARACTER_SPEED_LINE = 30.0
private const val CHARACTER_SPEED_ROTATE = 45

data class Speed(var line: Float, var rotate: Float)

open class Character(grid: Grid) {

    // !Сделать определение ширины и высоты жука програмным, чтобы не зависит от вида картинки
    val width = 24
    val height = 24

    var position = Coordinate(
        (0..grid.width).shuffled().first().toDouble(), (grid.height - 1)
            .toDouble()
    )
    var speed = Speed(0F, 0F)
    var angle = 90F

    var moves: MutableList<Point> = mutableListOf()
    var move: Point = Point(0, 0)
    var lastDirection = 1

    var deleteRow = 0

    val posTileX: Int
        get() = Math.round(position.x).toInt()

    val posTileY: Int
        get() = Math.round(position.y).toInt()

    val posTile
        get() = Point(posTileX, posTileY)

    val directionX
        get() = Math.round(Math.cos(this.angle * (Math.PI / 180))).toInt()

    val directionY
        get() = Math.round(Math.sin(this.angle * (Math.PI / 180))).toInt()


    open fun update(grid: Grid): String {
        changePosition()

        if (isNewFrame())
            return updateNewFrame(grid)

        return "true"
    }

    fun changePosition() {
        if (speed.rotate == 0F) {
            position = position.plus(
                Coordinate(
                    speed.line * directionX.toDouble(),
                    speed.line * directionY.toDouble()
                )
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

    fun updateNewFrame(grid: Grid): String {
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

    fun getSpeedAngle(): Speed {
        val tempAngle = Math.atan2(move.y.toDouble(), move.x.toDouble()) * (180 / Math.PI)
        var sign = 1
        if ((angle - tempAngle) > 0 && (angle - tempAngle) < 180)
            sign = -1

        if (Math.round(Math.cos(angle * (Math.PI / 180))).toInt() == move.x
            && Math.round(Math.sin(angle * (Math.PI / 180))).toInt() == move.y
        )
            return Speed((1 / 10.0).toFloat(), 0F)

        if (angle == tempAngle.toFloat())
            return Speed(0F, 0F)

        return Speed(0F, (sign * CHARACTER_SPEED_ROTATE).toFloat())
    }

    fun getDirection(grid: Grid): MutableList<Point> {
        // Проверяем свободен ли выбранный путь при фиксации фигуры
        if (deleteRow == 1
            && moves == isCanMove(arrayOf(moves.toTypedArray()), grid)
        )
            deleteRow = 0

        if (moves.isEmpty() || deleteRow == 1)
            return getNewDirection(grid)

        return moves
    }

    fun getNewDirection(grid: Grid): MutableList<Point> {
        val direction = DIRECTION()
        deleteRow = 0
        // Если двигаемся вправо
        if (((speed.line == 0F && speed.rotate == 0F) && move.x == 1) || move.x == 1) {
            lastDirection = 1
            return isCanMove(
                arrayOf(*direction.RIGHTDOWN, *direction.RIGHT, *direction.LEFT), grid
            ).toMutableList()
        }
        // Если двигаемся влево
        if (((speed.line == 0F && speed.rotate == 0F) && move.x == -1) || move.x == -1) {
            lastDirection = -1
            return isCanMove(arrayOf(*direction.LEFTDOWN, *direction.LEFT, *direction.RIGHT), grid)
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

    open fun isCanMove(arrayDirectionses: Array<Array<Point>>, grid: Grid): Array<Point> {
        for (directions in arrayDirectionses)
            if (isCanDirections(directions, grid))
                return directions

        return arrayOf(Point(0, 0))
    }

    fun isCanDirections(directions: Array<Point>, grid: Grid): Boolean {
        var addPoint = Point(0, 0)
        for (direction in directions) {
            addPoint = addPoint.plus(direction)
            val point = posTile.plus(addPoint)
            if (grid.isOutside(point) || grid.isNotFree(point))
                return false
        }

        return true
    }

    // Исходя из данных определяет спрайт для рисования
    open fun getSprite(): Point {
        if (angle == 0F && speed.line != 0F && getframe(position.x) == -1)
            return Point(2, 0)
        if (angle == 0F && speed.line != 0F)
            return Point(getframe(position.x), 1)

        if (angle == 180F && speed.line != 0F && getframe(position.x) == -1)
            return Point(6, 0)
        if (angle == 180F && speed.line != 0F)
            return Point(4 - getframe(position.x), 2)

        if (angle == 90F && speed.line != 0F && getframe(position.y) == -1)
            return Point(0, 0)
        if (angle == 90F && speed.line != 0F)
            return Point(getframe(position.y), 4)

        if (angle == 270F && speed.line != 0F && getframe(position.y) == -1)
            return Point(4, 0)
        if (angle == 270F && speed.line != 0F)
            return Point(getframe(position.y), 3)

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
    val LEFTDOWN: Array<Array<Point>> = arrayOf(_0D, LD, RD),
    val RIGHTDOWN: Array<Array<Point>> = arrayOf(_0D, RD, LD),
    val LEFT: Array<Array<Point>> = arrayOf(L0, LU, LUU),
    val RIGHT: Array<Array<Point>> = arrayOf(R0, RU, RUU),
)

fun getframe(coor: Double): Int {
    if (coor % 1 > 0.01 && coor % 1 < 0.99)
        return Math.floor((coor % 1) * NUMBER_FRAMES_BEEATLE_MOVE).toInt()

    return -1
}
