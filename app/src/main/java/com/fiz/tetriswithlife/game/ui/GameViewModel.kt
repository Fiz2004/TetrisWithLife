package com.fiz.tetriswithlife.game.ui

import android.view.MotionEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.tetriswithlife.game.data.RecordRepository
import com.fiz.tetriswithlife.game.domain.Controller
import com.fiz.tetriswithlife.game.domain.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.math.min

private const val widthGrid: Int = 13
private const val heightGrid: Int = 25

@HiltViewModel
class GameViewModel @Inject constructor(var recordRepository: RecordRepository) : ViewModel() {

    lateinit var gameState: GameState
    private val controller = Controller()
    private var job: Job? = null

    private var prevTime = System.currentTimeMillis()
    private var ending = 1.0
    var running = false

    fun tryLoadState(gameState: GameState?) {
        if (!this::gameState.isInitialized)
            this.gameState = gameState
                ?: GameState(widthGrid, heightGrid, recordRepository.loadRecord())
    }

    fun startGame(activity: GameActivity) {

        viewModelScope.launch(Dispatchers.Default) {
            job?.cancelAndJoin()
            job = viewModelScope.launch(Dispatchers.Default) {
                running = true

                while (isActive) {
                    if (running) {

                        stateUpdate()

                        activity.displayUpdate()

                    }
                }
            }
        }
    }

    private fun stateUpdate() {
        val now = System.currentTimeMillis()
        val deltaTime = min(now - prevTime, 100).toInt() / 1000.0

        if (gameState.status != "pause") {
            var status = true
            if (ending == 1.0)
                status = gameState.update(controller, deltaTime) {
                    if (gameState.scores > recordRepository.loadRecord()) {
                        gameState.record = gameState.scores
                        recordRepository.saveRecord(gameState.record)
                    }
                }

            if (!status || ending != 1.0)
                ending -= deltaTime
        }

        if (ending < 0 || gameState.status == "new game") {
            gameState.new(recordRepository.loadRecord())
            ending = 1.0
        }

        prevTime = now
    }

    fun clickLeftButton(event: MotionEvent) {
        job?.let {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> controller
                    .actionLeft()
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> controller
                    .actionCancel()
            }
        }
    }

    fun clickRightButton(event: MotionEvent) {
        job?.let {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> controller
                    .actionRight()
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> controller
                    .actionCancel()
            }
        }
    }

    fun clickDownButton(event: MotionEvent) {
        job?.let {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> controller
                    .actionDown()
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> controller
                    .actionCancel()
            }
        }
    }

    fun clickRotateButton(event: MotionEvent) {
        job?.let {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> controller
                    .actionUp()
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> controller
                    .actionCancel()
            }
        }
    }

    fun clickNewGameButton() {
        job?.let {
            gameState.status = "new game"
        }
    }

    fun clickPauseButton() {
        job?.let {
            gameState.clickPause()
        }
    }

    fun activityStop() {
        running = false
    }
}