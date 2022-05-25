package com.fiz.tetriswithlife.gameScreen.ui.models

import com.fiz.tetriswithlife.gameScreen.game.Game
import com.fiz.tetriswithlife.gameScreen.game.Grid
import com.fiz.tetriswithlife.gameScreen.game.character.Character
import com.fiz.tetriswithlife.gameScreen.game.figure.CurrentFigure
import com.fiz.tetriswithlife.gameScreen.game.figure.Figure
import java.io.Serializable

data class GameForSaveInstanceState(
    val width: Int,
    val height: Int,
    var grid: Grid,
    var character: Character,
    var nextFigure: Figure,
    var currentFigure: CurrentFigure,
    var status: Game.Companion.StatusGame,
    var scores: Int,
    var lastTime: Long,
    var timeToRestart: Double,
    var timeLast: Double
) : Serializable {
    companion object {
        fun fromGame(game: Game): GameForSaveInstanceState {
            return GameForSaveInstanceState(
                width = game.width,
                height = game.height,
                grid = game.grid,
                character = game.character,
                nextFigure = game.nextFigure,
                currentFigure = game.currentFigure,
                status = game.status,
                scores = game.scores,
                lastTime = game.lastTime,
                timeToRestart = game.timeToRestart,
                timeLast = game.timeLastUpdateController,
            )
        }
    }

    fun toGame(game: Game): Game {
        game.width = width
        game.height = height
        game.grid = grid
        game.character = character
        game.nextFigure = nextFigure
        game.currentFigure = currentFigure
        game.status = status
        game.scores = scores
        game.lastTime = lastTime
        game.timeToRestart = timeToRestart
        game.timeLastUpdateController = timeLast

        return game
    }
}