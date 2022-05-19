package com.fiz.tetriswithlife.game.domain.figure

import com.fiz.tetriswithlife.game.domain.grid.Grid
import com.fiz.tetriswithlife.game.domain.models.Cell
import com.fiz.tetriswithlife.game.domain.models.Coordinate
import com.fiz.tetriswithlife.game.domain.models.Figure
import com.fiz.tetriswithlife.game.domain.models.Point
import kotlin.math.ceil

private const val START_STEP_MOVE_AUTO = 0.001
private const val ADD_STEP_MOVE_AUTO = 0.01
private const val STEP_MOVE_KEY_X = 1
const val STEP_MOVE_KEY_Y = 0.01

class CurrentFigure(
    private val grid: Grid,
    var figure: Figure,
    getStartX: () -> Int = { (0 until (grid.width - figure.getMaxX())).shuffled().first() },
    var stepMoveAuto: Double = START_STEP_MOVE_AUTO,
    var position: Coordinate = Coordinate(
        getStartX().toDouble(),
        (0 - figure.getMaxY()).toDouble()
    ),
    var statusLastMovedDown: StatusMoved = StatusMoved.Fall
) {

    fun getPositionTile(p: Coordinate = Coordinate(position.x, position.y)): List<Point> {
        return figure.cells.map { it.point + p.toPoint() }
    }

    fun fixation(scores: Int) {
        val scoresForLevel = 300
        stepMoveAuto = ADD_STEP_MOVE_AUTO + ADD_STEP_MOVE_AUTO * (scores / scoresForLevel.toFloat())
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

    fun moveLeft() {
        if (!isCollission(Coordinate((position.x - STEP_MOVE_KEY_X), position.y)))
            position = position.copy(x = position.x - STEP_MOVE_KEY_X)
    }

    fun moveRight() {
        if (!isCollission(Coordinate((position.x + STEP_MOVE_KEY_X), position.y)))
            position = position.copy(x = position.x + STEP_MOVE_KEY_X)
    }

    fun rotate() {
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

    fun moveDown(stepY: Float) {
        val yStart = ceil(position.y)
        val yEnd = ceil(position.y + stepY.toDouble())
        val yMax = getYMax(yStart.toInt(), yEnd.toInt())

        if (isCheckCollisionIfMoveDown(yStart.toInt(), yEnd.toInt())) {

            position = position.copy(y = yMax.toDouble())

            statusLastMovedDown = StatusMoved.Fixation
            return
        }

        position =
            position.copy(y = position.y + (if (stepY < 1) stepY else yMax - yStart).toFloat())

        statusLastMovedDown = StatusMoved.Fall
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

    companion object{
        enum class StatusMoved {
            Fixation, Fall
        }
    }
}
