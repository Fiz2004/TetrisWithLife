package com.fiz.tetriswithlife.game.domain

import com.fiz.tetriswithlife.game.domain.figure.CurrentFigure
import com.fiz.tetriswithlife.game.domain.figure.STEP_MOVE_KEY_Y
import com.fiz.tetriswithlife.game.domain.grid.Grid
import com.fiz.tetriswithlife.game.domain.models.Cell
import com.fiz.tetriswithlife.game.domain.models.Coordinate
import com.fiz.tetriswithlife.game.domain.models.Point
import javax.inject.Inject
import kotlin.math.ceil

const val SecTimeOutInput = 0.08

private const val STEP_MOVE_KEY_X = 1

class UpdateCurrentFigureUseCase @Inject constructor() {

    private var timeLast: Double = 0.0

    operator fun invoke(
        grid: Grid,
        currentFigure: CurrentFigure,
        deltaTime: Double,
        controller: Controller
    ) {
        if (isCanTimeLast(deltaTime)) {
            if (controller.left)
                moveLeft(grid, currentFigure)

            if (controller.right)
                moveRight(grid, currentFigure)

            if (controller.up)
                rotate(grid, currentFigure)
        }

        val step: Float =
            if (controller.down) STEP_MOVE_KEY_Y.toFloat() else currentFigure.stepMoveAuto.toFloat()

        moveDown(grid, currentFigure, step)
    }

    private fun isCanTimeLast(deltaTime: Double): Boolean {
        if (timeLast == 0.0) {
            timeLast = SecTimeOutInput
        } else {
            timeLast -= deltaTime
            if (timeLast < 0.0)
                timeLast = 0.0
            return false
        }
        return true
    }


    private fun moveLeft(grid: Grid, currentFigure: CurrentFigure) {
        if (!isCollission(
                grid, currentFigure,
                Coordinate(
                    (currentFigure.position.x - STEP_MOVE_KEY_X),
                    currentFigure.position.y
                )
            )
        )
            currentFigure.position =
                currentFigure.position.copy(x = currentFigure.position.x - STEP_MOVE_KEY_X)
    }

    fun moveRight(grid: Grid, currentFigure: CurrentFigure) {
        if (!isCollission(
                grid, currentFigure,
                Coordinate(
                    (currentFigure.position.x + STEP_MOVE_KEY_X),
                    currentFigure.position.y
                )
            )
        )
            currentFigure.position =
                currentFigure.position.copy(x = currentFigure.position.x + STEP_MOVE_KEY_X)
    }

    fun rotate(grid: Grid, currentFigure: CurrentFigure) {
        val oldCells = currentFigure.figure.cells
        currentFigure.figure =
            currentFigure.figure.copy(cells = currentFigure.figure.cells.map { cell ->
                Cell(
                    Point(
                        3 - cell.point.y,
                        cell.point.x
                    ), cell.view
                )
            })
        if (isCollission(
                grid, currentFigure,
                Coordinate(
                    currentFigure.position.x,
                    currentFigure.position.y
                )
            )
        )
            currentFigure.figure = currentFigure.figure.copy(cells = oldCells)
    }

    fun moveDown(grid: Grid, currentFigure: CurrentFigure, stepY: Float) {
        val yStart = ceil(currentFigure.position.y)
        val yEnd = ceil(currentFigure.position.y + stepY.toDouble())
        val yMax = getYMax(grid, currentFigure, yStart.toInt(), yEnd.toInt())

        if (isCheckCollisionIfMoveDown(grid, currentFigure, yStart.toInt(), yEnd.toInt())) {

            currentFigure.position = currentFigure.position.copy(y = yMax.toDouble())

            currentFigure.statusLastMovedDown = CurrentFigure.Companion.StatusMoved.Fixation
            return
        }

        currentFigure.position =
            currentFigure.position.copy(y = currentFigure.position.y + (if (stepY < 1) stepY else yMax - yStart).toFloat())

        currentFigure.statusLastMovedDown = CurrentFigure.Companion.StatusMoved.Fall
    }

    private fun getYMax(grid: Grid, currentFigure: CurrentFigure, yStart: Int, yEnd: Int): Int {
        for (y in yStart..yEnd)
            if (isCollission(
                    grid,
                    currentFigure,
                    Coordinate(currentFigure.position.x, y.toDouble())
                )
            )
                return y - 1

        return yEnd
    }

    private fun isCheckCollisionIfMoveDown(
        grid: Grid,
        currentFigure: CurrentFigure,
        yStart: Int,
        yEnd: Int
    ): Boolean {
        for (y in yStart..yEnd)
            if (isCollission(
                    grid,
                    currentFigure,
                    Coordinate(currentFigure.position.x, y.toDouble())
                )
            )
                return true

        return false
    }

    fun isCollission(grid: Grid, currentFigure: CurrentFigure, p: Coordinate): Boolean {
        if (currentFigure.getPositionTile(p).any { point ->
                (point.x !in 0 until grid.width)
                        || point.y > grid.height - 1
            })
            return true

        if (currentFigure.getPositionTile(p).any { point ->
                grid.isInside(point) && grid.space[point.y][point.x].block != 0
            }
        )
            return true

        return false
    }
}