package com.fiz.tetriswithlife.game.domain.character

import com.fiz.tetriswithlife.game.domain.grid.Grid
import com.fiz.tetriswithlife.game.domain.models.Point

private const val PROBABILITY_EAT = 20

open class CharacterEat(grid: Grid) : Character(grid) {
    var eat = 0

    override fun update(grid: Grid, deltaTime: Double): StatusCharacter {
        val tempEat = this.eat
        eat = 0
        val statusUpdate = super.update(grid, deltaTime)

        if (this.isNewFrame()) {
            if (tempEat == 1)
                return StatusCharacter.EatFinish

            return StatusCharacter.Nothing
        }

        if (tempEat == 1 && this.isMoveStraight()) {
            eat = 1
            return StatusCharacter.Eat
        }

        return statusUpdate
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

    override fun isCanMove(listDirections: List<List<Point>>, grid: Grid): List<Point> {
        for (directions in listDirections)
            if (isCanDirections(directions, grid, (0..100).shuffled().first() < PROBABILITY_EAT))
                return directions
        return listOf(Point(0, 0))
    }

    private fun isCanDirections(directions: List<Point>, grid: Grid, isDestroy: Boolean): Boolean {
        val result = mutableListOf<Point>()
        var addPoint = Point(0, 0)
        for (direction in directions) {
            addPoint = addPoint.plus(direction)
            val point = posTile.plus(addPoint)

            if (grid.isOutside(point))
                return false

            result += direction

            if (grid.isNotFree(point)) {
                if (addPoint.y == 0 && isDestroy) {
                    eat = 1
                    return true
                }
                return false
            }
        }

        return true
    }

    override fun getSprite(): Point {
        if (eat == 0)
            return super.getSprite()

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
}

enum class StatusCharacter {
    Nothing, Eat, EatFinish
}
