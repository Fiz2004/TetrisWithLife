package com.fiz.tetriswithlife.gameScreen.game.figure

import com.fiz.tetriswithlife.gameScreen.domain.models.Controller
import com.fiz.tetriswithlife.gameScreen.game.Coordinate
import com.fiz.tetriswithlife.gameScreen.game.Vector
import java.io.Serializable
import kotlin.math.ceil
import kotlin.random.Random

private const val START_STEP_MOVE_AUTO = 1.0
private const val ADD_STEP_MOVE_AUTO = 3.0

private const val STEP_MOVE_KEY_X = 1
private const val STEP_MOVE_KEY_Y = 10.0
private const val SCORES_FOR_LEVEL = 300.0

private const val SEC_TIME_OUT_INPUT = 0.08

class CurrentFigure private constructor(
    _figure: Figure,
    startPosition: Coordinate,
) : Serializable {

    var position: Coordinate = startPosition
        private set

    var figure: Figure = _figure
        private set

    var statusLastMovedDown: StatusMoved = StatusMoved.Fall
        private set

    val isStatusLastMovedDownFixation
        get() = statusLastMovedDown == StatusMoved.Fixation

    val isStatusLastMovedDownFall
        get() = statusLastMovedDown == StatusMoved.Fall

    private val tileY: Int
        get() = ceil(position.y).toInt()

    private var stepMoveAuto: Double = START_STEP_MOVE_AUTO

    private var timeLastUpdateController: Double = 0.0

    fun updateStepMoveAuto(scores: Int) {
        stepMoveAuto = ADD_STEP_MOVE_AUTO + ADD_STEP_MOVE_AUTO * (scores / SCORES_FOR_LEVEL)
    }

    private fun fixation(yMax: Int) {
        position = position.copy(y = yMax.toDouble())

        statusLastMovedDown = StatusMoved.Fixation
    }

    private fun fall(addPositionY: Double) {
        val newY = position.y + addPositionY

        position = position.copy(y = newY)

        statusLastMovedDown = StatusMoved.Fall
    }

    private fun getStepMoveY(controllerDown: Boolean): Double {
        return if (controllerDown) STEP_MOVE_KEY_Y
        else stepMoveAuto
    }

    fun getPositionTile(position: Coordinate = Coordinate(this.position.x, this.position.y)) =
        figure.cells.map { it.vector + position.toPoint }

    private fun getTileYIfMoveDownByStep(stepY: Double) = ceil(position.y + stepY).toInt()

    fun update(deltaTime: Double, controller: Controller, isCollisionPoint: (Vector) -> Boolean) {
        if (isCanTimeLast(deltaTime)) {
            currentFigureMoveLeft(controller, isCollisionPoint)
            currentFigureMoveRight(controller, isCollisionPoint)
            currentFigureRotate(controller, isCollisionPoint)
        }

        currentFigureMoveDown(controller, deltaTime, isCollisionPoint)
    }

    private fun isCanTimeLast(deltaTime: Double): Boolean {
        if (timeLastUpdateController == 0.0) {
            timeLastUpdateController = SEC_TIME_OUT_INPUT
            return true
        }

        timeLastUpdateController -= deltaTime
        if (timeLastUpdateController < 0.0) timeLastUpdateController = 0.0
        return false
    }

    private fun currentFigureMoveLeft(
        controller: Controller, isCollisionPoint: (Vector) -> Boolean
    ) {
        if (controller.left) {
            val newPosition = position.copy(x = position.x - STEP_MOVE_KEY_X)
            if (!isCollisionForCurrentFigure(newPosition, isCollisionPoint)) {
                position = newPosition
            }
        }
    }

    private fun currentFigureMoveRight(
        controller: Controller, isCollisionPoint: (Vector) -> Boolean
    ) {
        if (controller.right) {
            val newPosition = position.copy(x = position.x + STEP_MOVE_KEY_X)
            if (!isCollisionForCurrentFigure(newPosition, isCollisionPoint)) {
                position = newPosition
            }
        }
    }

    private fun currentFigureRotate(controller: Controller, isCollisionPoint: (Vector) -> Boolean) {
        if (controller.up) {
            val oldFigure = figure
            figure = figure.copy(cells = figure.getCellsRotate())
            if (isCollisionForCurrentFigure(position, isCollisionPoint)) {
                figure = oldFigure
            }
        }
    }

    private fun currentFigureMoveDown(
        controller: Controller, deltaTime: Double, isCollisionPoint: (Vector) -> Boolean
    ) {
        val step = getStepMoveY(controller.down) * deltaTime
        moveDown(step, isCollisionPoint)
    }

    private fun moveDown(stepY: Double, isCollisionPoint: (Vector) -> Boolean) {
        val yStart = tileY
        val yEnd = getTileYIfMoveDownByStep(stepY)
        val yMax = getYByCollisionIfMoveDown(yStart, yEnd, isCollisionPoint)

        if (yMax == yEnd) {

            val addPositionY = if (stepY < 1) stepY
            else (yMax - yStart).toDouble()

            fall(addPositionY)

            return
        }

        fixation(yMax)
    }

    private fun getYByCollisionIfMoveDown(
        yStart: Int, yEnd: Int, isCollisionPoint: (Vector) -> Boolean
    ): Int {
        (yStart..yEnd).forEach { y ->
            val coordinate = Coordinate(position.x, y.toDouble())
            if (isCollisionForCurrentFigure(coordinate, isCollisionPoint))
                return y - 1
        }

        return yEnd
    }

    private fun isCollisionForCurrentFigure(
        coordinate: Coordinate, isCollision: Vector.() -> Boolean
    ): Boolean {
        return getPositionTile(coordinate).any { point ->
            point.isCollision()
        }
    }

    companion object {

        fun create(
            gridWidth: Int, figure: Figure = Figure(), coordinate: Coordinate = Coordinate(
                Random.nextInt(gridWidth - figure.getMaxX()).toDouble(),
                (0 - figure.getMaxY()).toDouble()
            )
        ): CurrentFigure {
            return CurrentFigure(figure, coordinate)
        }

        enum class StatusMoved {
            Fixation, Fall
        }
    }
}
