package com.fiz.tetriswithlife.gameScreen.ui

import android.graphics.Color
import com.fiz.tetriswithlife.R
import com.fiz.tetriswithlife.gameScreen.game.character.TIMES_BREATH_LOSE
import com.fiz.tetriswithlife.gameScreen.ui.models.*
import java.io.Serializable
import kotlin.math.floor
import kotlin.math.max

const val SecTimeForRestartForEndGame = 1.0

data class GameState(
    val backgroundsUi: List<BackgroundUi>,
    val blocksUi: List<BlockUi>,
    val characterUi: CharacterUi,
    val blocksCurrentFigureUi: List<CurrentFigureUi>,
    val blocksNextFigureUi: List<NextFigureUi>,
    val scores: Int,
    val status: StatusCurrentGame,
    val record: Int,
) {

    val textForScores: String = getScore(scores)
    val textForRecord: String = getRecord(record)
    val textResourceForPauseResumeButton: Int = getTextForPauseResumeButton(status)
    val visibilityForInfoBreathTextView: Boolean =
        getVisibilityForInfoBreathTextView(characterUi.breath)
    val textForInfoBreathTextView: String = getTextForBreathTextView(
        characterUi.breath,
        characterUi.secondsSupplyForBreath
    )
    val colorForInfoBreathTextView: Int = getColorForBreathTextView(
        characterUi.breath,
        characterUi.secondsSupplyForBreath
    )

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

    companion object {
        enum class StatusCurrentGame : Serializable {
            Playing, Pause
        }
    }
}


