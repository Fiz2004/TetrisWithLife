package com.fiz.tetriswithlife.game.domain.models.figure

import com.fiz.tetriswithlife.game.domain.models.Coordinate
import com.fiz.tetriswithlife.game.domain.models.Vector
import java.io.Serializable
import kotlin.math.ceil

private const val START_STEP_MOVE_AUTO = 1.0
private const val ADD_STEP_MOVE_AUTO = 3.0

const val SecTimeOutInput = 0.08

private const val STEP_MOVE_KEY_X = 1
private const val STEP_MOVE_KEY_Y = 10.0

data class CurrentFigure(
    var figure: Figure,
    var position: Coordinate,
    private var stepMoveAuto: Double = START_STEP_MOVE_AUTO,
    private var statusLastMovedDown: StatusMoved = StatusMoved.Fall
) : Serializable {

    fun updateStepMoveAuto(scores: Int) {
        val scoresForLevel = 300
        stepMoveAuto = ADD_STEP_MOVE_AUTO + ADD_STEP_MOVE_AUTO * (scores / scoresForLevel.toFloat())
    }

    fun getPositionMoveLeft(): Coordinate {
        return position.copy(x = position.x - STEP_MOVE_KEY_X)
    }

    fun getPositionMoveRight(): Coordinate {
        return position.copy(x = position.x + STEP_MOVE_KEY_X)
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

    fun isStatusLastMovedDownFixation(): Boolean {
        return statusLastMovedDown == StatusMoved.Fixation
    }

    fun isStatusLastMovedDownFall(): Boolean {
        return statusLastMovedDown == StatusMoved.Fall
    }

    fun getStepMoveY(controllerDown: Boolean): Float {
        return if (controllerDown)
            STEP_MOVE_KEY_Y.toFloat()
        else
            stepMoveAuto.toFloat()
    }

    fun getPositionTile(
        p: Coordinate = Coordinate(
            position.x,
            position.y
        )
    ): List<Vector> {
        return figure.cells.map { it.vector + p.toPoint() }
    }

    fun getTileY():Int{
        return ceil(position.y).toInt()
    }

    fun getTileYIfMoveDownByStep(stepY: Double):Int{
        return ceil(position.y + stepY).toInt()
    }

    fun getFigureRotate(): Figure {
        return figure.copy(cells = figure.getCellsRotate())
    }

    companion object {
        enum class StatusMoved {
            Fixation, Fall
        }
    }
}
