package com.fiz.tetriswithlife.game.domain.figure

import com.fiz.tetriswithlife.game.domain.Controller
import com.fiz.tetriswithlife.game.domain.grid.Coordinate
import com.fiz.tetriswithlife.game.domain.grid.Grid
import kotlin.math.ceil

private const val START_STEP_MOVE_AUTO = 0.03
private const val ADD_STEP_MOVE_AUTO = 0.1
private const val STEP_MOVE_KEY_X = 1
private const val STEP_MOVE_KEY_Y = 4

class CurrentFigure(
    private val grid: Grid,
    var figure: Figure,
    getStartX: () -> Int = { (0 until (grid.width - figure.getWidth())).shuffled().first() },
    private var stepMoveAuto: Double = START_STEP_MOVE_AUTO,
    val position: Coordinate = Coordinate(
        getStartX().toDouble(),
        (0 - figure.getHeight()).toDouble()
    )
) {

    fun getPositionTile(p: Coordinate = Coordinate(position.x, position.y)): List<Point> {
        return figure.cells.map { it.point + p.toPoint() }
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
        if (controller.left) moveLeft()
        if (controller.right) moveRight()
        if (controller.up) rotate()
        val step: Float = if (controller.down) STEP_MOVE_KEY_Y.toFloat() else stepMoveAuto.toFloat()
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
        val oldCells = figure.cells
        figure = figure.copy(cells = figure.cells.map { cell ->
            Cell(
                Point(
                    3 - cell.point.y,
                    cell.point.x
                ), cell.view
            )
        })
        if (isCollission(Coordinate(position.x, position.y)))
            figure = figure.copy(cells = oldCells)
    }

    private fun moveDown(stepY: Float): String {
        val yStart = ceil(position.y)
        val yEnd = ceil(position.y + stepY.toDouble())
        val yMax = getYMax(yStart.toInt(), yEnd.toInt())

        if (isCheckCollisionIfMoveDown(yStart.toInt(), yEnd.toInt())) {
            if (getPositionTile(Coordinate(position.x, yMax.toDouble()))
                    .any { (it.y - 1) < 0 }
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
