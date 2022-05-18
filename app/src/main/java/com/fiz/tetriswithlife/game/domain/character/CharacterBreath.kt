package com.fiz.tetriswithlife.game.domain.character

import com.fiz.tetriswithlife.game.domain.grid.Grid
import com.fiz.tetriswithlife.game.domain.models.Point

// Время без дыхания для проигрыша
const val TIMES_BREATH_LOSE = 60.0

class CharacterBreath(grid: Grid) : CharacterEat(grid) {
    var timeBreath = TIMES_BREATH_LOSE
    var breath = true

    private var tempGrid: Array<Array<Int>> = Array(grid.height) {
        Array(grid.width) {
            0
        }
    }

    private fun refreshTempGrid(grid: Grid) {
        grid.space.forEachIndexed { indexY, arrayOfElements ->
            arrayOfElements.forEachIndexed { indexX, element ->
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

        for (shiftPoint in arrayOf(Point(0, -1), Point(1, 0), Point(-1, 0), Point(0, 1))) {
            val nextElement = tile + shiftPoint
            if (isInside(nextElement) && isFree(nextElement)
                && findWay(nextElement, grid)
            )
                return true
        }
        return false
    }
}
