package com.fiz.tetriswithlife

class CharacterBreath(grid: Grid) : CharacterEat(grid) {

    var timeBreath = System.currentTimeMillis()
    var breath = true

    fun isBreath(grid: Grid): Boolean {
        breath = findWay(posTile, emptyArray(), grid)

        if (breath)
            timeBreath = System.currentTimeMillis()

        return breath
    }

    fun findWay(tile: Point, TempCash: Array<Point>, grid: Grid): Boolean {
        if (tile.y == 0F)
            return true

        var cash: Array<Point> = TempCash.clone()
        cash += Point(tile.x, tile.y)

        for (element in arrayOf(Point(0, -1), Point(1, 0), Point(-1, 0), Point(0, 1)))
            if (grid.isInside(tile.plus(element)) && grid.isFree(tile.plus(element))
                && cash.find { tile.x + element.x == it.x && tile.y + element.y == it.y } == null
                && findWay(tile.plus(element), cash, grid)
            )
                return true

        return false
    }
}
