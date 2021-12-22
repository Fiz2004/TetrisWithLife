package com.fiz.tetriswithlife

private const val NUMBER_FRAMES_BEEATLE_MOVE = 5;
private const val CHARACTER_SPEED_LINE = 30;
private const val CHARACTER_SPEED_ROTATE = 45;

data class Speed(var line: Float, var rotate: Float)

open class Character(grid: Grid) {

    // !Сделать определение ширины и высоты жука програмным, чтобы не зависит от вида картинки
    val width = 24
    val height = 24

    val position = Point((0..grid.width).shuffled().first(), grid.height.toInt() - 1)
    val speed = Speed(0F, 0F)
    var angle = 90F

    var moves: Array<Point>? = null
    val move = Point(0, 0)
    val lastDirection = 1

    var deleteRow = 0

    val posTileX
        get() = Math.round(position.x)

    val posTileY
        get() = Math.round(position.y)

    val posTile
        get() = Point(posTileX, posTileY)

    val directionX
        get() = Math.round(Math.cos(this.angle * (Math.PI / 180)))

    val directionY
        get() = Math.round(Math.sin(this.angle * (Math.PI / 180)))


    fun update(grid: Grid): Boolean {
        changePosition();

        if (isNewFrame())
            return updateNewFrame(grid);

        return true;
    }

    fun changePosition() {
        if (speed.rotate == 0F) {
            position.x += speed.line * directionX
            position.y += speed.line * directionY
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

    fun updateNewFrame(grid: Grid): Boolean {
        moves = getDirection(grid)

        if (move.x == moves[0].x && move.y == moves[0].y) {
            if (isMoveStraight())
                move = moves.toMutableList().removeAt(0)
        } else {
            [move] = moves;
        }

        speed = getSpeedAngle()

        return true
    }

    fun isMoveStraight(): Boolean {
        return directionX.toFloat() == move.x && directionY.toFloat() == move.y
    }

    fun getSpeedAngle() {
        val angle = Math.atan2(move.y, move.x) * (180 / Math.PI)
        val sign = 1
        if ((this.angle - angle) > 0 && (this.angle - angle) < 180)
            sign = -1

        if (Math.round(Math.cos(this.angle * (Math.PI / 180))) === move.x
            && Math.round(Math.sin(this.angle * (Math.PI / 180))) === move.y
        )
            return Speed((1 / 10).toFloat(), 0F)

        if (this.angle === angle)
            return Speed(0F, 0F)

        return Speed(0F, (sign * CHARACTER_SPEED_ROTATE).toFloat())
    }

    getDirectionMovement()
    {
        val direction = Point(directionX.toFloat(), directionY.toFloat())
        if (directionX === -1 && directionY === 1) {
            direction.x = -1;
            direction.y = 0;
        }
        if (directionX === -1 && directionY === -1) {
            direction.x = 0;
            direction.y = -1;
        }
        if (directionX === 1 && directionY === 1) {
            direction.x = 0;
            direction.y = 1;
        }
        if (directionX === 1 && directionY === -1) {
            direction.x = 1;
            direction.y = 0;
        }
        return `${[this.direction.x, this.direction.y, this.move.x, this.move.y].join('')}`;
    }

    fun getDirection(grid: Grid): Array<Point> {
        // Проверяем свободен ли выбранный путь при фиксации фигуры
        if (deleteRow == 1
            && moves == isCanMove(*moves, grid)
        )
            deleteRow = 0;

        if (moves.size == 0 || deleteRow == 1)
            return getNewDirection(grid);

        return moves;
    }

    fun getNewDirection(grid:Grid):Array<Point>    {
        val direction = DIRECTION();
        deleteRow = 0
        // Если двигаемся вправо
        if ((
                    (speed.line == 0F && speed.rotate == 0F)
                            && move.x == 1F
                    ) || move.x == 1F
        ) {
            lastDirection = 1;
            return isCanMove([... DIRECTION . RIGHTDOWN, ...DIRECTION.RIGHT, ...DIRECTION.LEFT], grid);
        }
        // Если двигаемся влево
        if (((speed.line == 0F && speed.rotate == 0F)
                    && move.x == -1F
                    ) || move.x == -1F
        ) {
            lastDirection = -1;
            return isCanMove([... DIRECTION . LEFTDOWN, ...DIRECTION.LEFT, ...DIRECTION.RIGHT], grid);
        }

        if (lastDirection == -1)
            return isCanMove(arrayOf(*direction._0D, *direction.LEFT, *direction.RIGHT), grid);

        return isCanMove([...[DIRECTION['0D']], ...DIRECTION.RIGHT, ...DIRECTION.LEFT], grid);
    }

    fun isCanMove(arrayDirectionses:Array<Array<Point>>, grid: Grid): Array<Point> {
        for (directions in arrayDirectionses)
            if (isCanDirections(directions, grid))
                return directions;

        return arrayOf(Point(0, 0));
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
    fun getSprite():Point {
        if (angle == 0F && speed.line != 0F && getframe(position.x) == -1)
            return Point( 2,  0)
        if (angle == 0F && speed.line != 0F)
            return Point(getframe(position.x), 1 )

        if (angle == 180F && speed.line != 0F && getframe(position.x) == -1)
            return Point ( 6, 0 )
        if (angle == 180F && speed.line != 0F)
            return Point( 4-getframe(position.x),  2 )

        if (angle == 90F && speed.line != 0F && getframe(position.y) == -1)
            return Point ( 0, 0 )
        if (angle == 90F && speed.line != 0F)
            return Point( getframe(position.y),  4 )

        if (angle == 270F && speed.line != 0F && getframe(position.y) == -1)
            return Point ( 4, 0 )
        if (angle == 270F && speed.line != 0F)
            return Point(getframe(position.y), 3 )

        if (speed.rotate != 0F && angle == 0F)
            return Point ( 2, 0 )
        if (speed.rotate != 0F && angle == 45F)
            return Point ( 1, 0 )
        if (speed.rotate != 0F && angle == 90F)
            return Point ( 0, 0 )
        if (speed.rotate != 0F && angle == 135F)
            return Point ( 7, 0 )
        if (speed.rotate != 0F && angle == 180F)
            return Point ( 6, 0 )
        if (speed.rotate != 0F && angle == 225F)
            return Point ( 5, 0 )
        if (speed.rotate != 0F && angle == 270F)
            return Point ( 4, 0 )
        if (speed.rotate != 0F && angle == 315F)
            return Point ( 3, 0 )

        return Point ( 0, 0 )
    }
}

data class DIRECTION(
    val L: Point = Point(-1, 0),
    val R: Point = Point(1, 0),
    val D: Point = Point(0, 1),
    val U: Point = Point(0, -1),
    val _0: Point = Point(0, 0),
    val _0D: Array<Point> = arrayOf(Point(0, 1)),
    val RD: Array<Point> = arrayOf(D, R),
    val LD: Array<Point> = arrayOf(D, L),
    val R0: Array<Point> = arrayOf(R),
    val L0: Array<Point> = arrayOf(L),
    val RU: Array<Point> = arrayOf(U, R),
    val LU: Array<Point> = arrayOf(U, L),
    val RUU: Array<Point> = arrayOf(U, U, R),
    val LUU: Array<Point> = arrayOf(U, U, L),
    val LEFTDOWN: Array<Point> = arrayOf(*_0D, *LD, *RD),
    val RIGHTDOWN: Array<Point> = arrayOf(*_0D, *RD, *LD),
    val LEFT: Array<Point> = arrayOf(*L0, *LU, *LUU),
    val RIGHT: Array<Point> = arrayOf(*R0, *RU, *RUU),
)

fun getframe(coor: Float): Int {
    if (coor % 1 > 0.01 && coor % 1 < 0.99)
        return Math.floor((coor.toDouble() % 1) * NUMBER_FRAMES_BEEATLE_MOVE).toInt()

    return -1;
}
