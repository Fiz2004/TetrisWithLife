package com.fiz.tetriswithlife.figure

import com.fiz.tetriswithlife.*
import com.fiz.tetriswithlife.grid.Cell
import com.fiz.tetriswithlife.grid.Coordinate
import com.fiz.tetriswithlife.grid.Grid
import com.fiz.tetriswithlife.grid.Point
import kotlin.math.ceil

private const val START_STEP_MOVE_AUTO = 0.03
private const val ADD_STEP_MOVE_AUTO = 0.1
private const val STEP_MOVE_KEY_X = 1
private const val STEP_MOVE_KEY_Y = 4

class CurrentFigure(
  private val grid: Grid,
  figure: Figure,
  startX: () -> Int = { (0 until (grid.width - figure.getWidth())).shuffled().first() }
) : Figure() {
  private var stepMoveAuto = START_STEP_MOVE_AUTO
  val position = createStartPosition(startX)

  init {
    cells = figure.cells.clone()
  }

  private fun createStartPosition(startX: () -> Int): Coordinate {
    return Coordinate(
      startX().toDouble(),
      (0 - getHeight()).toDouble()
    )
  }

  fun getPositionTile(p: Coordinate = Coordinate(position.x, position.y)): Array<Point> {
    return cells.map { cell -> Point(cell.x + p.x.toInt(), cell.y + p.y.toInt()) }.toTypedArray()
  }

  fun fixation(scores: Int) {
    val scoresForLevel = 300
    stepMoveAuto = ADD_STEP_MOVE_AUTO
    +ADD_STEP_MOVE_AUTO * (scores / scoresForLevel.toFloat())
  }

  fun isCollission(p: Coordinate): Boolean {
    if (getPositionTile(p).any { point ->
        (point.x !in 0 until grid.width)
                || point.y > grid.height - 1
      })
      return true

    if (getPositionTile(p).any { point ->
        grid.isInside(point) && grid.space[point.y][point.x].block != 0
      }
    )
      return true

    return false
  }

  fun moves(controller: Controller): String {
    if (controller.Left) moveLeft()
    if (controller.Right) moveRight()
    if (controller.Up) rotate()
    val step: Float = if (controller.Down) STEP_MOVE_KEY_Y.toFloat() else stepMoveAuto.toFloat()
    return moveDown(step)
  }

  private fun moveLeft() {
    if (!isCollission(Coordinate((position.x - STEP_MOVE_KEY_X), position.y)))
      position.x -= STEP_MOVE_KEY_X
  }

  private fun moveRight() {
    if (!isCollission(Coordinate((position.x + STEP_MOVE_KEY_X), position.y)))
      position.x += STEP_MOVE_KEY_X
  }

  private fun rotate() {
    val oldCells = cells
    cells = cells.map { cell -> Cell(3 - cell.y, cell.x, cell.view) }.toTypedArray()
    if (isCollission(Coordinate(position.x, position.y)))
      cells = oldCells
  }

  private fun moveDown(stepY: Float): String {
    val yStart = ceil(position.y)
    val yEnd = ceil(position.y + stepY.toDouble())
    val yMax = getYMax(yStart.toInt(), yEnd.toInt())

    if (isCheckCollisionIfMoveDown(yStart.toInt(), yEnd.toInt())) {
      if (getPositionTile(Coordinate(position.x, yMax.toDouble()))
          .any { p -> (p.y - 1) < 0 }
      )
        return "endGame"
      position.y = yMax.toDouble()

      return "fixation"
    }

    position.y += (if (stepY < 1) stepY else yMax - yStart).toFloat()
    return "fall"
  }

  private fun getYMax(yStart: Int, yEnd: Int): Int {
    for (y in yStart..yEnd)
      if (isCollission(Coordinate(position.x, y.toDouble())))
        return y - 1

    return yEnd
  }

  private fun isCheckCollisionIfMoveDown(yStart: Int, yEnd: Int): Boolean {
    for (y in yStart..yEnd)
      if (isCollission(Coordinate(position.x, y.toDouble())))
        return true

    return false
  }
}
