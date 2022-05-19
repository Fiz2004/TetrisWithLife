package com.fiz.tetriswithlife.game.domain

import com.fiz.tetriswithlife.game.data.RecordRepository
import com.fiz.tetriswithlife.game.ui.GameState
import com.fiz.tetriswithlife.game.ui.heightGrid
import com.fiz.tetriswithlife.game.ui.widthGrid
import javax.inject.Inject

private const val SecTimeForRestartForEndGame = 1.0

class UpdateGameStateForTimeUseCase @Inject constructor(
    private val recordRepository: RecordRepository
) {
    private var timeToRestart = SecTimeForRestartForEndGame

    operator fun invoke(
        gameState: GameState,
        deltaTime: Double,
        controller: Controller
    ): GameState {
        if (gameState.isStatusPause())
            return gameState

        if (timeToRestart < 0 || gameState.isStatusNewGame()) {
            timeToRestart = SecTimeForRestartForEndGame
            return GameState(widthGrid, heightGrid, recordRepository.loadRecord())
        }

        val status = if (isGameContinue())
            updateGameState(gameState, controller, deltaTime, updateRecord())
        else
            GameState.Companion.StatusUpdateGame.End

        if (status == GameState.Companion.StatusUpdateGame.End)
            timeToRestart -= deltaTime

        return gameState
    }

    private fun isGameContinue(): Boolean {
        return timeToRestart == SecTimeForRestartForEndGame
    }

    private fun updateRecord() = { score: Int ->

        if (score > recordRepository.loadRecord())
            recordRepository.saveRecord(score)

    }

    fun updateGameState(
        gameState: GameState, controller: Controller,
        deltaTime: Double,
        updateRecord: (Int) -> Unit
    ): GameState.Companion.StatusUpdateGame {
        if (!gameState.character.breath)
            gameState.character.timeBreath -= deltaTime

        if (controller.isCannotTimeLast(deltaTime))
            return GameState.Companion.StatusUpdateGame.Continue

        val status = gameState.currentFigure.moves(controller)

        if (gameState.isEndGame(status, updateRecord)) {
            gameState.checkRecord(updateRecord)
            return GameState.Companion.StatusUpdateGame.End
        }

        if (!gameState.character.breath && (gameState.isLose() || gameState.isCrushedBeetle())) {
            gameState.checkRecord(updateRecord)
            return GameState.Companion.StatusUpdateGame.End
        }

        if (gameState.status == GameState.Companion.StatusCurrentGame.NewGame) {
            gameState.checkRecord(updateRecord)
            return GameState.Companion.StatusUpdateGame.End
        }

        val statusCharacter = gameState.character.update(gameState.grid)
        if (statusCharacter == "eatFinish") {
            val tile = gameState.character.posTile
            gameState.grid.space[tile.y][tile.x].setZero()
            gameState.scores += 50
            gameState.checkRecord(updateRecord)
            gameState.character.isBreath(gameState.grid)
        } else if (statusCharacter == "eatDestroy") {
            gameState.changeGridDestroyElement()
        }

        return GameState.Companion.StatusUpdateGame.Continue
    }
}