package com.fiz.tetriswithlife.game.domain.models

import java.io.Serializable

private const val START_STEP_MOVE_AUTO = 0.001
private const val ADD_STEP_MOVE_AUTO = 0.01

class CurrentFigure(
    var figure: Figure,
    val width: Int,
    getStartX: () -> Int = { (0 until (width - figure.getMaxX())).shuffled().first() },
    var stepMoveAuto: Double = START_STEP_MOVE_AUTO,
    var position: Coordinate = Coordinate(
        getStartX().toDouble(),
        (0 - figure.getMaxY()).toDouble()
    ),
    var statusLastMovedDown: StatusMoved = StatusMoved.Fall
) : Serializable {

    fun getPositionTile(p: Coordinate = Coordinate(position.x, position.y)): List<Point> {
        return figure.cells.map { it.point + p.toPoint() }
    }

    fun fixation(scores: Int) {
        val scoresForLevel = 300
        stepMoveAuto = ADD_STEP_MOVE_AUTO + ADD_STEP_MOVE_AUTO * (scores / scoresForLevel.toFloat())
    }

    companion object {
        enum class StatusMoved {
            Fixation, Fall
        }
    }
}
