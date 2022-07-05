package com.fiz.tetriswithlife.gameScreen.game

import com.fiz.tetriswithlife.gameScreen.domain.models.Controller
import com.fiz.tetriswithlife.gameScreen.domain.repositories.RecordRepository
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

const val PROBABILITY_EAT_PERCENT = 20

private const val mSEC_FROM_FPS_60 = ((1.0 / 60.0) * 1000.0).toLong()

@Singleton
class Game @Inject constructor(private val recordRepository: RecordRepository) {
    var status: StatusGame = StatusGame.Playing
        private set

    var scores: Int = 0
        private set

    var lastTime: Long = System.currentTimeMillis()
        private set

    var actors: Actors = Actors()
        private set

    fun update(controller: Controller, infoGame: (Int) -> Unit) {
        val deltaTime = getDeltaTime()
        if (deltaTime == 0.0) return

        when (status) {
            StatusGame.Playing -> gameLoop(deltaTime, controller,infoGame)
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
        deltaTime: Double, controller: Controller, infoGame: (Int) -> Unit
    ) {
        actors.update(deltaTime, controller, { scores }, ::plusScores)
        if (actors.actorsStatus == ActorsStatus.NewGame) {
            infoGame(scores)
            newGame()
        }
    }

    private fun plusScores(score: Int) {
        scores += score
        if (scores > recordRepository.loadRecord()) recordRepository.saveRecord(scores)
    }

    private fun newGame() {
        actors = Actors()
        scores = 0
        status = StatusGame.Playing
    }

    fun clickNewGame() {
        status = StatusGame.NewGame
    }

    fun clickPause() {
        status = when (status) {
            StatusGame.Playing -> StatusGame.Pause
            StatusGame.Pause -> StatusGame.Playing
            StatusGame.NewGame -> return
        }
    }

    fun loadState(
        actors: Actors,
        status: StatusGame,
        scores: Int,
        lastTime: Long,
    ) {
        this.actors = actors
        this.status = status
        this.scores = scores
        this.lastTime = lastTime
    }

    companion object {

        enum class StatusGame : Serializable {
            Playing, Pause, NewGame
        }

    }
}
