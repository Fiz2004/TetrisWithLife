package com.fiz.tetriswithlife.game.domain

import com.fiz.tetriswithlife.game.data.RecordRepository
import com.fiz.tetriswithlife.game.domain.character.StatusCharacter
import com.fiz.tetriswithlife.game.domain.figure.CurrentFigure
import com.fiz.tetriswithlife.game.domain.models.Figure
import com.fiz.tetriswithlife.game.domain.models.Point
import com.fiz.tetriswithlife.game.ui.GameState
import com.fiz.tetriswithlife.game.ui.heightGrid
import com.fiz.tetriswithlife.game.ui.widthGrid
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.roundToInt

private const val SecTimeForRestartForEndGame = 1.0

private const val NUMBER_FRAMES_ELEMENTS = 4

class UpdateGameStateForTimeUseCase @Inject constructor(
    private val updateCurrentFigureUseCase: UpdateCurrentFigureUseCase,
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

        updateCurrentFigureUseCase(gameState.grid, gameState.currentFigure, deltaTime, controller)
        updateCharacter(gameState, deltaTime, updateRecord)

        if (isEndGame(gameState, updateRecord)) {
            gameState.checkRecord(updateRecord)
            return GameState.Companion.StatusUpdateGame.End
        }

        return GameState.Companion.StatusUpdateGame.Continue
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
            changeGridDestroyElement(gameState)
        }
    }

    private fun isEndGame(
        gameState: GameState,
        updateRecord: (Int) -> Unit
    ): Boolean {
        if (isEndGameFigure(gameState, updateRecord))
            return true

        if (!gameState.character.breath && (isLose(gameState) || isCrushedBeetle(gameState)))
            return true

        if (gameState.isStatusNewGame())
            return true

        return false
    }


    private fun isLose(gameState: GameState): Boolean {
        if (gameState.grid.isNotFree(gameState.character.posTile) && gameState.character.eat == 0)
            return true

        if (gameState.character.timeBreath <= 0)
            return true

        return false
    }

    private fun isEndGameFigure(
        gameState: GameState,
        updateRecord: (Int) -> Unit
    ): Boolean {

        if ((gameState.currentFigure.statusLastMovedDown == CurrentFigure.Companion.StatusMoved.Fixation &&
                    (gameState.currentFigure.getPositionTile()
                        .any { (it.y - 1) < 0 }
                            ))// Фигура достигла препятствия
            || (gameState.currentFigure.statusLastMovedDown == CurrentFigure.Companion.StatusMoved.Fall && isCrushedBeetle(
                gameState
            ))
        )
        // Стакан заполнен игра окончена
            return true


        if (gameState.currentFigure.statusLastMovedDown == CurrentFigure.Companion.StatusMoved.Fixation) {
            fixation(gameState, updateRecord)
            createCurrentFigure(gameState)
        }

        return false
    }


    private fun createCurrentFigure(gameState: GameState) {
        gameState.currentFigure = CurrentFigure(gameState.nextFigure, gameState.grid.width)
        gameState.nextFigure = Figure()
    }

    fun isCrushedBeetle(gameState: GameState): Boolean {
        val tile = gameState.character.posTile
        for (elem in gameState.currentFigure.getPositionTile())
            if (elem == tile
                || (gameState.grid.isNotFree(tile) && gameState.character.eat == 0)
            )
                return true

        return false
    }

    private fun fixation(gameState: GameState, updateRecord: (Int) -> Unit) {
        val tile = gameState.currentFigure.getPositionTile()
        for ((index, value) in tile.withIndex())
            gameState.grid.space[value.y][value.x].block =
                gameState.currentFigure.figure.cells[index].view
        val countRowFull = gameState.grid.getCountRowFull()
        if (countRowFull != 0)
            gameState.grid = gameState.grid.deleteRows()
        val scoresForRow = 100
        for (i in 1..countRowFull)
            gameState.scores += i * scoresForRow

        gameState.currentFigure.fixation(gameState.scores)
        gameState.checkRecord(updateRecord)

        gameState.character.deleteRow = 1
        gameState.character.isBreath(gameState.grid)
    }

    fun changeGridDestroyElement(gameState: GameState) {
        val newX = if (gameState.character.move.x == -1)
            0
        else
            gameState.character.move.x

        val offset = Point(newX, gameState.character.move.y)

        val tile = Point(
            floor(gameState.character.position.x).toInt() + offset.x,
            (gameState.character.position.y.roundToInt() + offset.y),
        )

        gameState.grid.space[tile.y][tile.x].status[gameState.character.getDirectionEat()] =
            getStatusDestroyElement(gameState) + 1

        gameState.character.isBreath(gameState.grid)
    }

    private fun getStatusDestroyElement(gameState: GameState): Int {
        if (gameState.character.angle == 0F)
            return floor((gameState.character.position.x % 1) * NUMBER_FRAMES_ELEMENTS.toDouble())
                .toInt()
        if (gameState.character.angle == 180F)
            return 3 - floor((gameState.character.position.x % 1) * NUMBER_FRAMES_ELEMENTS.toDouble())
                .toInt()

        return 0
    }
}

