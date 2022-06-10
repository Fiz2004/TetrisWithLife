package com.fiz.tetriswithlife.gameScreen.ui.models

import com.fiz.tetriswithlife.gameScreen.game.Actors
import com.fiz.tetriswithlife.gameScreen.game.Game
import java.io.Serializable

data class GameForSaveInstanceState(
    val actors: Actors,
    val status: Game.Companion.StatusGame,
    val scores: Int,
    val lastTime: Long,
) : Serializable {
    companion object {
        fun fromGame(game: Game): GameForSaveInstanceState {
            return GameForSaveInstanceState(
                actors = game.actors,
                status = game.status,
                scores = game.scores,
                lastTime = game.lastTime,
            )
        }
    }

    fun toGame(game: Game): Game {
        game.loadState(
            actors,
            status,
            scores,
            lastTime,
        )

        return game
    }
}