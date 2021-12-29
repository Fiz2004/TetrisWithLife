package com.fiz.tetriswithlife.character

import com.fiz.tetriswithlife.grid.Grid
import com.fiz.tetriswithlife.grid.Point

class CharacterBreath(grid: Grid) : CharacterEat(grid) {

  var timeBreath = System.currentTimeMillis()
  var breath = true

  fun isBreath(grid: Grid): Boolean {
    breath = findWay(posTile, emptyArray(), grid)

    if (breath)
      timeBreath = System.currentTimeMillis()

    return breath
  }

  private fun findWay(tile: Point, TempCash: Array<Point>, grid: Grid): Boolean {
    if (tile.y == 0)
      return true

    var cash: Array<Point> = TempCash.clone()
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
