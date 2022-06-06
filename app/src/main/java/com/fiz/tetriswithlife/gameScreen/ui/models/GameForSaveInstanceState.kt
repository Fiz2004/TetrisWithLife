package com.fiz.tetriswithlife.gameScreen.ui.models

import com.fiz.tetriswithlife.gameScreen.game.Game
import com.fiz.tetriswithlife.gameScreen.game.Grid
import com.fiz.tetriswithlife.gameScreen.game.figure.Figure
import java.io.Serializable

data class GameForSaveInstanceState(
    val width: Int,
    val height: Int,
    val grid: Grid,
    val nextFigure: Figure,
    val status: Game.Companion.StatusGame,
    val scores: Int,
    val lastTime: Long,
) : Serializable {
    companion object {
        fun fromGame(game: Game): GameForSaveInstanceState {
            return GameForSaveInstanceState(
                width = game.width,
                height = game.height,
                grid = game.grid,
                nextFigure = game.nextFigure,
                status = game.status,
                scores = game.scores,
                lastTime = game.lastTime,
            )
        }
    }

    fun toGame(game: Game): Game {
        game.loadState(
            width,
            height,
            grid,
            nextFigure,
            status,
            scores,
            lastTime,
        )

        return game
    }
}