package com.fiz.tetriswithlife.game.ui

import android.view.MotionEvent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.tetriswithlife.game.data.RecordRepository
import com.fiz.tetriswithlife.game.domain.Controller
import com.fiz.tetriswithlife.game.domain.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlin.math.min

const val widthGrid: Int = 13
const val heightGrid: Int = 25

@HiltViewModel
class GameViewModel @Inject constructor(var recordRepository: RecordRepository) : ViewModel() {
    var gameState: MutableStateFlow<GameState> = MutableStateFlow(GameState(widthGrid, heightGrid, recordRepository.loadRecord())); private set

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

            if (gameState.value.status != "pause") {
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

            if (ending < 0 || gameState.value.status == "new game") {
                gameState.value.new(recordRepository.loadRecord())
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
            gameState.value.status = "new game"
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