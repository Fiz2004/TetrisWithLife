package com.fiz.tetriswithlife.game.ui

import android.graphics.Color
import android.view.MotionEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.tetriswithlife.R
import com.fiz.tetriswithlife.game.data.RecordRepository
import com.fiz.tetriswithlife.game.domain.Controller
import com.fiz.tetriswithlife.game.domain.character.TIMES_BREATH_LOSE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

const val widthGrid: Int = 13
const val heightGrid: Int = 25

data class UIState(
    val scores: String = "000000",
    val record: String = "000000",
    val pauseResumeButton: Int = 0,
    val infoBreathTextViewVisibility: Boolean = false,
    val textForBreathTextView: String = "0",
    val colorForBreathTextView: Int = 0,

    )

fun scoresFormat(scores: Int): String {
    return scores.toString().padStart(6, '0')
}

fun recordFormat(record: Int): String {
    return record.toString().padStart(6, '0')
}

fun textForPauseButton(status: GameState.Companion.StatusGame): Int {
    return if (status == GameState.Companion.StatusGame.Pause)
        R.string.resume_game_button
    else
        R.string.pause_game_button
}

fun getInfoBreathTextViewVisibility(breath: Boolean): Boolean {
    return !breath
}

private fun getSec(breath: Boolean, timeBreath: Double): Double {
    return if (breath)
        TIMES_BREATH_LOSE
    else
        max(timeBreath, 0.0)
}

fun getTextForBreathTextView(breath: Boolean, timeBreath: Double): String {
    val sec = getSec(breath, timeBreath)
    return sec.toInt().toString()
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

@HiltViewModel
class GameViewModel @Inject constructor(var recordRepository: RecordRepository) : ViewModel() {

    var gameState: MutableStateFlow<GameState> =
        MutableStateFlow(GameState(widthGrid, heightGrid, recordRepository.loadRecord()))
        private set

    var uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState()); private set

    init {
        gameState.onEach {
            uiState.value = uiState.value.copy(
                scores = scoresFormat(it.scores),
                record = recordFormat(it.record),
                pauseResumeButton = textForPauseButton(it.status),
                infoBreathTextViewVisibility = getInfoBreathTextViewVisibility(it.character.breath),
                textForBreathTextView = getTextForBreathTextView(
                    it.character.breath,
                    it.character.timeBreath
                ),
                colorForBreathTextView = getColorForBreathTextView(
                    it.character.breath,
                    it.character.timeBreath
                ),
            )
        }.launchIn(viewModelScope)
    }

    private var controller = Controller()
    private var job: Job? = null

    private var prevTime = System.currentTimeMillis()
    private var ending = 1.0
    var running = false

    fun tryLoadState(gameState: GameState?) {
        if (gameState != null)
            this.gameState.value = gameState
    }

    fun startGame() {

        viewModelScope.launch(Dispatchers.Default) {
            job?.cancelAndJoin()
            job = viewModelScope.launch(Dispatchers.Default) {
                running = true

                while (isActive) {
                    if (running) {
                        stateUpdate()
                    }
                }
            }
        }
    }

    private fun stateUpdate() {
            val now = System.currentTimeMillis()
            val deltaTime = min(now - prevTime, 100).toInt() / 1000.0
            if (deltaTime == 0.0) return

            if (gameState.value.status != GameState.Companion.StatusGame.Pause) {
                var status = true
                if (ending == 1.0)
                    status = gameState.value.update(controller, deltaTime) {
                        if (gameState.value.scores > recordRepository.loadRecord()) {
                            gameState.value.record = gameState.value.scores
                            recordRepository.saveRecord(gameState.value.record)
                        }
                    }

                if (!status || ending != 1.0)
                    ending -= deltaTime
            }

            if (ending < 0 || gameState.value.status == GameState.Companion.StatusGame.NewGame) {
                gameState.value = GameState(widthGrid, heightGrid, recordRepository.loadRecord())
                ending = 1.0
            }

            gameState.value=gameState.value.copy(changed=!gameState.value.changed)

            prevTime = now
    }

    fun clickLeftButton(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> controller = Controller(left = true)
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> controller = Controller()
        }
    }

    fun clickRightButton(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> controller = Controller(right = true)
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> controller = Controller()
        }
    }

    fun clickDownButton(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> controller = Controller(down = true)
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> controller = Controller()
        }
    }

    fun clickRotateButton(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> controller = Controller(up = true)
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> controller = Controller()
        }
    }

    fun clickNewGameButton() {
        job?.let {
            gameState.value.status = GameState.Companion.StatusGame.NewGame
        }
    }

    fun clickPauseButton() {
        job?.let {
            gameState.value.clickPause()
        }
    }

    fun activityStop() {
        running = false
    }
}