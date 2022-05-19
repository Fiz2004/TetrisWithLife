package com.fiz.tetriswithlife.game.ui

import com.fiz.tetriswithlife.game.domain.character.CharacterBreath
import com.fiz.tetriswithlife.game.domain.figure.CurrentFigure
import com.fiz.tetriswithlife.game.domain.grid.Grid
import com.fiz.tetriswithlife.game.domain.models.Figure
import java.io.Serializable

data class GameState(
    val width: Int,
    val height: Int,
    val startRecord: Int,
    var grid: Grid = Grid(width, height),
    var character: CharacterBreath = CharacterBreath(grid),
    var scores: Int = 0,
    var record: Int = startRecord,
    val status: StatusCurrentGame = StatusCurrentGame.Playing,
    var nextFigure: Figure = Figure(),
    var currentFigure: CurrentFigure = CurrentFigure(nextFigure, width),
    val changed: Boolean = false
) : Serializable {

    fun isStatusPause(): Boolean {
        return status == StatusCurrentGame.Pause
    }

    fun isStatusNewGame(): Boolean {
        return status == StatusCurrentGame.NewGame
    }

    fun checkRecord(updateRecord: (Int) -> Unit) {
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
        enum class StatusCurrentGame {
            Playing, Pause, NewGame
        }

        enum class StatusUpdateGame {
            Continue, End
        }
    }
}


