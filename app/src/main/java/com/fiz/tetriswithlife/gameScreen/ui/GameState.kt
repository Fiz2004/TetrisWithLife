package com.fiz.tetriswithlife.gameScreen.ui

import android.graphics.Color
import com.fiz.tetriswithlife.R
import com.fiz.tetriswithlife.gameScreen.domain.models.Element
import com.fiz.tetriswithlife.gameScreen.domain.models.Grid
import com.fiz.tetriswithlife.gameScreen.domain.models.character.Character
import com.fiz.tetriswithlife.gameScreen.domain.models.character.TIMES_BREATH_LOSE
import com.fiz.tetriswithlife.gameScreen.domain.models.figure.CurrentFigure
import com.fiz.tetriswithlife.gameScreen.domain.models.figure.Figure
import java.io.Serializable
import kotlin.math.floor
import kotlin.math.max

const val SecTimeForRestartForEndGame = 1.0

data class GridState(
    val width: Int,
    val height: Int,
    val space: List<List<Element>>,
    val character: Character,
    val nextFigure: Figure,
    val currentFigure: CurrentFigure
) : Serializable {
    companion object {
        fun fromGrid(grid: Grid): GridState {
            return GridState(
                width = grid.width,
                height = grid.height,
                space = grid.space,
                character = grid.character,
                nextFigure = grid.nextFigure,
                currentFigure = grid.currentFigure
            )
        }
    }
}

data class GameState(
    val gridState: GridState,
    val scores: Int = 0,
    val record: Int,
    val status: StatusCurrentGame = StatusCurrentGame.Playing,
    val changed: Boolean = false,
    var timeToRestart: Double = SecTimeForRestartForEndGame
) : Serializable {

    val textForScores: String = getScore(scores)
    val textForRecord: String = getRecord(record)
    val textResourceForPauseResumeButton: Int = getTextForPauseResumeButton(status)
    val visibilityForInfoBreathTextView: Boolean =
        getVisibilityForInfoBreathTextView(gridState.character.breath.breath)
    val textForInfoBreathTextView: String = getTextForBreathTextView(
        gridState.character.breath.breath,
        gridState.character.breath.secondsSupplyForBreath
    )
    val colorForInfoBreathTextView: Int = getColorForBreathTextView(
        gridState.character.breath.breath,
        gridState.character.breath.secondsSupplyForBreath
    )

    fun isNewGame(): Boolean {
        return timeToRestart < 0 || isStatusNewGame()
    }

    private fun isStatusNewGame(): Boolean {
        return status == StatusCurrentGame.NewGame
    }

    fun isStatusPause(): Boolean {
        return status == StatusCurrentGame.Pause
    }

    fun isGameContinue(): Boolean {
        return timeToRestart == SecTimeForRestartForEndGame
    }

    fun getNewStatus(): StatusCurrentGame {
        return if (status == StatusCurrentGame.Playing)
            StatusCurrentGame.Pause
        else
            StatusCurrentGame.Playing
    }

    private fun getScore(scores: Int): String {
        return scores.toString().padStart(6, '0')
    }

    private fun getRecord(record: Int): String {
        return record.toString().padStart(6, '0')
    }

    private fun getTextForPauseResumeButton(status: StatusCurrentGame): Int {
        return if (status == StatusCurrentGame.Pause)
            R.string.resume_game_button
        else
            R.string.pause_game_button
    }

    private fun getVisibilityForInfoBreathTextView(breath: Boolean): Boolean {
        return !breath
    }

    private fun getTextForBreathTextView(breath: Boolean, timeBreath: Double): String {
        val sec = getSec(breath, timeBreath)
        return sec.toInt().toString()
    }

    private fun getSec(breath: Boolean, timeBreath: Double): Double {
        return if (breath)
            TIMES_BREATH_LOSE
        else
            max(timeBreath, 0.0)
    }

    private fun getColorForBreathTextView(breath: Boolean, timeBreath: Double): Int {
        val sec = getSec(breath, timeBreath)
        val color = 255 - ((floor(
            sec
        ) * 255) / TIMES_BREATH_LOSE).toInt()
        return Color.argb(
            color, 255, 0, 0
        )
    }

    fun gameEnd(deltaTime: Double) {
        timeToRestart -= deltaTime
    }

    companion object {
        enum class StatusCurrentGame : Serializable {
            Playing, Pause, NewGame
        }
    }
}


