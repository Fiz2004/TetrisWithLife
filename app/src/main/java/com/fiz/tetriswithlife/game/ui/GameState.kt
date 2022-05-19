package com.fiz.tetriswithlife.game.ui

import com.fiz.tetriswithlife.game.domain.models.Controller
import com.fiz.tetriswithlife.game.domain.models.Grid
import java.io.Serializable

private const val SecTimeForRestartForEndGame = 1.0

data class GameState(
    var grid: Grid,
    var scores: Int = 0,
    var record: Int,
    val status: StatusCurrentGame = StatusCurrentGame.Playing,
    val changed: Boolean = false,
    private var timeToRestart:Double = SecTimeForRestartForEndGame
) : Serializable {

    fun update(
        deltaTime: Double,
        controller: Controller,
        loadRecord: () -> Int,
        updateRecord: (Int) -> Unit
    ): GameState {
        if (isStatusPause())
            return this

        if (timeToRestart < 0 || isStatusNewGame()) {
            timeToRestart = SecTimeForRestartForEndGame
            return GameState(grid = Grid(widthGrid, heightGrid), record = loadRecord())
        }

        val status = when {
            isStatusNewGame() -> StatusUpdateGame.End
            isGameContinue() ->
                grid.updateActors(deltaTime, controller, loadScores = { scores }, plusScores = { score ->
                    scores += score
                    checkRecord(updateRecord)
                })
            else -> StatusUpdateGame.End
        }

        if (status == StatusUpdateGame.End)
            timeToRestart -= deltaTime

        return this
    }

    private fun isGameContinue(): Boolean {
        return timeToRestart == SecTimeForRestartForEndGame
    }

    private fun isStatusPause(): Boolean {
        return status == StatusCurrentGame.Pause
    }

    private fun isStatusNewGame(): Boolean {
        return status == StatusCurrentGame.NewGame
    }

    private fun checkRecord(updateRecord: (Int) -> Unit) {
        if (scores > record) {
            record = scores
            updateRecord(scores)
        }
    }

    fun getNewStatus(): StatusCurrentGame {
        return if (status == StatusCurrentGame.Playing)
            StatusCurrentGame.Pause
        else
            StatusCurrentGame.Playing
    }

    companion object {
        enum class StatusCurrentGame : Serializable {
            Playing, Pause, NewGame
        }

        enum class StatusUpdateGame {
            Continue, End
        }
    }
}


