package com.fiz.tetriswithlife.game.domain.character

import com.fiz.tetriswithlife.game.domain.grid.Grid
import com.fiz.tetriswithlife.game.domain.models.Point

// Время без дыхания для проигрыша
const val TIMES_BREATH_LOSE = 60.0

class CharacterBreath(grid: Grid) : CharacterEat(grid) {
    var timeBreath = TIMES_BREATH_LOSE
    var breath = true

    fun isBreath(grid: Grid): Boolean {
        val temp = breath
        breath = findWay(posTile, emptyArray(), grid)

        if (temp && !breath)
            timeBreath = TIMES_BREATH_LOSE

        return breath
    }

    private fun findWay(tile: Point, tempCash: Array<Point>, grid: Grid): Boolean {
        if (tile.y == 0)
            return true
        var cash: Array<Point> = tempCash.clone()
        cash += Point(tile.x, tile.y)

        for (shiftPoint in arrayOf(Point(0, -1), Point(1, 0), Point(-1, 0), Point(0, 1))) {
            val nextElement = tile + shiftPoint
            if (grid.isInside(nextElement) && grid.isFree(nextElement)
                && !cash.contains(nextElement) && findWay(nextElement, cash, grid)
            )
                return true
        }
        return false
    }
}
