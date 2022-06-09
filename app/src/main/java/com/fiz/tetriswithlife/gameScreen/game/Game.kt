package com.fiz.tetriswithlife.gameScreen.game

import com.fiz.tetriswithlife.gameScreen.domain.models.Controller
import com.fiz.tetriswithlife.gameScreen.domain.repositories.RecordRepository
import com.fiz.tetriswithlife.gameScreen.game.figure.Figure
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

const val WIDTH_GRID: Int = 10
const val HEIGHT_GRID: Int = 20

const val PROBABILITY_EAT_PERCENT = 20

private const val mSEC_FROM_FPS_60 = ((1.0 / 60.0) * 1000.0).toLong()

@Singleton
class Game @Inject constructor(
    var width: Int = WIDTH_GRID,
    var height: Int = HEIGHT_GRID,
    private val recordRepository: RecordRepository
) {
    var status: StatusGame = StatusGame.Playing
        private set

    var scores: Int = 0
        private set

    var lastTime: Long = System.currentTimeMillis()
        private set

    var grid: Grid = Grid.create(width, height)
        private set

    var nextFigure: Figure = Figure()
        private set

    private var loopStatus: LoopStatusGame = LoopStatusGame.Continue

    private fun newGame() {
        grid = Grid.create(width, height)
        nextFigure = Figure()
        scores = 0
        status = StatusGame.Playing
        loopStatus = LoopStatusGame.Continue
    }

    fun update(controller: Controller) {
        val deltaTime = getDeltaTime()
        if (deltaTime == 0.0) return

        when (status) {
            StatusGame.Playing -> gameLoop(deltaTime, controller)
            StatusGame.NewGame -> newGame()
            StatusGame.Pause -> return
        }
    }

    private fun getDeltaTime(): Double {
        val now = System.currentTimeMillis()
        val deltaTime = min(now - lastTime, mSEC_FROM_FPS_60) / 1000.0
        lastTime = now
        return deltaTime
    }

    private fun gameLoop(
        deltaTime: Double, controller: Controller
    ) {
        when (loopStatus) {
            LoopStatusGame.Continue -> {
                loopStatus = grid.update(deltaTime, controller, ::plusScores)
                if (loopStatus !is LoopStatusGame.End && grid.currentFigure.isStatusLastMovedDownFixation) {
                    grid.fixation(nextFigure, scores, ::plusScores)
                    nextFigure = Figure()
                }
            }
            is LoopStatusGame.End -> {
                loopStatus.timeToRestart -= deltaTime
                if (loopStatus.timeToRestart < 0) newGame()
            }
        }
    }

    private fun plusScores(score: Int) {
        scores += score
        if (scores > recordRepository.loadRecord()) recordRepository.saveRecord(scores)
    }

    fun clickPause() {
        if (status == StatusGame.NewGame) return
        status = if (status == StatusGame.Playing) StatusGame.Pause
        else StatusGame.Playing
    }

    fun clickNewGame() {
        status = StatusGame.NewGame
    }

    fun loadState(
        width: Int,
        height: Int,
        grid: Grid,
        nextFigure: Figure,
        status: StatusGame,
        scores: Int,
        lastTime: Long,
    ) {
        this.width = width
        this.height = height
        this.grid = grid
        this.nextFigure = nextFigure
        this.status = status
        this.scores = scores
        this.lastTime = lastTime
    }

    companion object {

        private const val SecTimeForRestartForEndGame = 1.0

        enum class StatusGame : Serializable {
            Playing, Pause, NewGame
        }

        sealed class LoopStatusGame(var timeToRestart: Double = SecTimeForRestartForEndGame) :
            Serializable {
            object Continue : LoopStatusGame()
            class End : LoopStatusGame(timeToRestart = SecTimeForRestartForEndGame)
        }
    }
}
