package com.fiz.tetriswithlife.gameScreen.game.figure

import com.fiz.tetriswithlife.gameScreen.game.Coordinate
import com.fiz.tetriswithlife.gameScreen.game.Grid
import java.io.Serializable
import kotlin.math.ceil
import kotlin.random.Random

private const val START_STEP_MOVE_AUTO = 1.0
private const val ADD_STEP_MOVE_AUTO = 3.0

private const val STEP_MOVE_KEY_X = 1
private const val STEP_MOVE_KEY_Y = 10.0
private const val SCORES_FOR_LEVEL = 300.0

class CurrentFigure private constructor(
    _figure: Figure,
    startPosition: Coordinate,
) : Serializable {

    val isStatusLastMovedDownFixation
        get() = statusLastMovedDown == StatusMoved.Fixation

    val isStatusLastMovedDownFall
        get() = statusLastMovedDown == StatusMoved.Fall

    var figure: Figure = _figure
        private set

    val tileY: Int
        get() = ceil(position.y).toInt()

    val positionIfMoveLeft: Coordinate
        get() = position.copy(x = position.x - STEP_MOVE_KEY_X)

    val positionIfMoveRight: Coordinate
        get() = position.copy(x = position.x + STEP_MOVE_KEY_X)

    var position: Coordinate = startPosition
        private set

    var statusLastMovedDown: StatusMoved = StatusMoved.Fall
        private set

    private var stepMoveAuto: Double = START_STEP_MOVE_AUTO

    fun updateStepMoveAuto(scores: Int) {
        stepMoveAuto = ADD_STEP_MOVE_AUTO + ADD_STEP_MOVE_AUTO * (scores / SCORES_FOR_LEVEL)
    }

    fun fixation(yMax: Int) {
        position = position.copy(y = yMax.toDouble())

        statusLastMovedDown = StatusMoved.Fixation
    }

    fun fall(addPositionY: Double) {
        val newY = position.y + addPositionY

        position = position.copy(y = newY)

        statusLastMovedDown = StatusMoved.Fall
    }

    fun getStepMoveY(controllerDown: Boolean): Double {
        return if (controllerDown)
            STEP_MOVE_KEY_Y
        else
            stepMoveAuto
    }

    fun getPositionTile(position: Coordinate = Coordinate(this.position.x, this.position.y)) =
        figure.cells.map { it.vector + position.toPoint() }

    fun getTileYIfMoveDownByStep(stepY: Double) = ceil(position.y + stepY).toInt()

    fun getFigureRotate(): Figure {
        return figure.copy(cells = figure.getCellsRotate())
    }

    fun setPosition(newPosition: Coordinate) {
        position = newPosition
    }

    fun setFigure(newFigure: Figure) {
        figure = newFigure
    }

    companion object {

        fun create(
            grid: Grid,
            figure: Figure = Figure(),
            coordinate: Coordinate = Coordinate(
                Random.nextInt(grid.space.first().size - figure.getMaxX()).toDouble(),
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
