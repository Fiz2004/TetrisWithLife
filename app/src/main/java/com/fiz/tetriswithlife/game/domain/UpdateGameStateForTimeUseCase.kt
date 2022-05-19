package com.fiz.tetriswithlife.game.domain

import com.fiz.tetriswithlife.game.data.RecordRepository
import com.fiz.tetriswithlife.game.domain.character.StatusCharacter
import com.fiz.tetriswithlife.game.domain.figure.CurrentFigure
import com.fiz.tetriswithlife.game.domain.figure.STEP_MOVE_KEY_Y
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

    private fun updateGameState(
        gameState: GameState, controller: Controller,
        deltaTime: Double,
        updateRecord: (Int) -> Unit
    ): GameState.Companion.StatusUpdateGame {

        updateCurrentFigure(gameState.currentFigure, deltaTime, controller)
        updateCharacter(gameState, deltaTime, updateRecord)

        if (isEndGame(gameState, updateRecord)) {
            gameState.checkRecord(updateRecord)
            return GameState.Companion.StatusUpdateGame.End
        }

        return GameState.Companion.StatusUpdateGame.Continue
    }

    private fun updateCurrentFigure(
        currentFigure: CurrentFigure,
        deltaTime: Double,
        controller: Controller
    ) {
        if (controller.isCanTimeLast(deltaTime)) {
            if (controller.left)
                currentFigure.moveLeft()

            if (controller.right)
                currentFigure.moveRight()

            if (controller.up)
                currentFigure.rotate()
        }

        val step: Float =
            if (controller.down) STEP_MOVE_KEY_Y.toFloat() else currentFigure.stepMoveAuto.toFloat()

        currentFigure.moveDown(step)
    }

    private fun updateCharacter(
        gameState: GameState,
        deltaTime: Double,
        updateRecord: (Int) -> Unit
    ) {

        val statusCharacter = gameState.character.update(gameState.grid, deltaTime)

        if (statusCharacter == StatusCharacter.EatFinish) {
            val tile = gameState.character.posTile
            gameState.grid.space[tile.y][tile.x].setZero()
            gameState.scores += 50
            gameState.checkRecord(updateRecord)
            gameState.character.isBreath(gameState.grid)
        }

        if (statusCharacter == StatusCharacter.Eat) {
            gameState.changeGridDestroyElement()
        }
    }

    private fun isEndGame(
        gameState: GameState,
        updateRecord: (Int) -> Unit
    ): Boolean {
        if (gameState.isEndGame(updateRecord))
            return true

        if (!gameState.character.breath && (gameState.isLose() || gameState.isCrushedBeetle()))
            return true

        if (gameState.isStatusNewGame())
            return true

        return false
    }
}