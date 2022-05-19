package com.fiz.tetriswithlife.game.domain

import android.graphics.Color
import com.fiz.tetriswithlife.R
import com.fiz.tetriswithlife.game.domain.character.TIMES_BREATH_LOSE
import com.fiz.tetriswithlife.game.ui.GameState
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.max

class FormatUseCase @Inject constructor() {
    fun getScore(scores: Int): String {
        return scores.toString().padStart(6, '0')
    }

    fun getRecord(record: Int): String {
        return record.toString().padStart(6, '0')
    }

    fun getTextForPauseResumeButton(status: GameState.Companion.StatusCurrentGame): Int {
        return if (status == GameState.Companion.StatusCurrentGame.Pause)
            R.string.resume_game_button
        else
            R.string.pause_game_button
    }

    fun getVisibilityForInfoBreathTextView(breath: Boolean): Boolean {
        return !breath
    }

    fun getTextForBreathTextView(breath: Boolean, timeBreath: Double): String {
        val sec = getSec(breath, timeBreath)
        return sec.toInt().toString()
    }

    private fun getSec(breath: Boolean, timeBreath: Double): Double {
        return if (breath)
            TIMES_BREATH_LOSE
        else
            max(timeBreath, 0.0)
    }

    fun getColorForBreathTextView(breath: Boolean, timeBreath: Double): Int {
        val sec = getSec(breath, timeBreath)
        val color = ((floor(
            sec
        ) * 255) / TIMES_BREATH_LOSE).toInt()
        return Color.rgb(
            255, color, color
        )
    }
}