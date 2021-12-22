package com.fiz.tetriswithlife

private const val NUMBER_FRAMES_BEEATLE_MOVE = 5
private const val PROBABILITY_EAT = 20

open class CharacterEat(grid: Grid) : Character(grid) {

    var eat = 0

    override fun update(grid: Grid): String {
        val tempEat = this.eat
        eat = 0
        val statusUpdate = super.update(grid)

        if (this.isNewFrame()) {
            if (tempEat == 1)
                return "eat"

            return "true"
        }

        if (tempEat == 1 && this.isMoveStraight()) {
            eat = 1
            return "eatDestroy"
        }

        return statusUpdate
    }

    fun getDirectionEat(): Char {
        if (move.x == -1F && move.y == 0F)
            return 'R'

        if (move.x == 1F && move.y == 0F)
            return 'L'

        if (move.x == 0F && move.y == 1F)
            return 'U'

        throw Exception("Error: incorrect value function getDirectionEat")
    }

    fun isEatingNow(): Boolean {
        return eat == 1 && !isNewFrame()
                && (isMoveStraight())
    }

    override fun isCanMove(arrayDirectionses: Array<Array<Point>>, grid: Grid): Array<Point> {
        for (directions in arrayDirectionses)
            if (isCanDirections(directions, grid, (0..100).shuffled().first() < PROBABILITY_EAT))
                return directions

        return arrayOf(Point(0, 0))
    }

    fun isCanDirections(directions: Array<Point>, grid: Grid, isDestoy: Boolean): Boolean {
        var result = emptyArray<Point>()
        var addPoint = Point(0, 0)
        for (direction in directions) {
            addPoint = addPoint.plus(direction)
            val point = posTile.plus(addPoint)

            if (grid.isOutside(point))
                return false

            result+=direction

            if (grid.isNotFree(point)) {
                if (addPoint.y == 0F && isDestoy) {
                    eat = 1
                    return true
                }
                return false
            }
        }

        return true
    }

    override fun getSprite():Point {
        if (eat==0)
            return super.getSprite()

        if (angle == 0F && speed.line != 0F && getframe(position.x) == -1)
            return Point ( 2, 0 )
        if (angle == 0F && speed.line != 0F)
            return Point (getframe(position.x),  5)

        if (angle == 180F && speed.line != 0F && getframe(position.x) == -1)
            return Point ( 6, 0 )
        if (angle == 180F && speed.line != 0F)
            return Point (4-getframe(position.x),  6 )

        if (angle == 90F && speed.line != 0F && getframe(position.y) == -1)
            return Point ( 0, 0 )
        if (angle == 90F && speed.line != 0F)
            return Point ( getframe(position.y), 8 )

        if (angle == 270F && speed.line != 0F && getframe(position.y) == -1)
            return Point ( 4, 0 )
        if (angle == 270F && speed.line != 0F)
            return Point (getframe(this.position.y),  7)

        return Point ( 0, 0 )
    }
}
