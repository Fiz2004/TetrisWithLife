package com.fiz.tetriswithlife.figure

import com.fiz.tetriswithlife.grid.Cell
import com.fiz.tetriswithlife.grid.Point

private const val NUMBER_IMAGES_FIGURE = 5
val FIGURE: Array<Array<Point>> = arrayOf(
  arrayOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(3, 1)),
  arrayOf(Point(1, 1), Point(2, 1), Point(2, 2), Point(3, 2)),
  arrayOf(Point(1, 1), Point(2, 1), Point(2, 2), Point(2, 3)),
  arrayOf(Point(1, 1), Point(1, 2), Point(2, 2), Point(2, 3)),
  arrayOf(Point(1, 1), Point(1, 2), Point(2, 2), Point(1, 3)),
  arrayOf(Point(1, 1), Point(1, 2), Point(2, 1), Point(2, 2)),
  arrayOf(Point(1, 1), Point(2, 1), Point(1, 2), Point(1, 3))
)

open class Figure(
  numberFigure: () -> Int = {
    (FIGURE.indices).shuffled().first()
  }
) {
  var cells: Array<Cell> = createFigure(numberFigure)

  private fun createFigure(
    numberFigure: () -> Int
  ): Array<Cell> {
    var result: Array<Cell> = emptyArray()
    for (p in FIGURE[numberFigure()]) {
      val view = (1..NUMBER_IMAGES_FIGURE).shuffled().first()
      result += Cell(p.x, p.y, view)
    }
    return result
  }

  fun getWidth(): Int {
    return cells.reduce { a, b -> if (a.x > b.x) a else b }.x
  }

  fun getHeight(): Int {
    return cells.reduce { a, b -> if (a.y > b.y) a else b }.y
  }
}


