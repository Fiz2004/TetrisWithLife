package com.fiz.tetriswithlife.game.ui

import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.tetriswithlife.game.data.RecordRepository
import com.fiz.tetriswithlife.game.domain.Controller
import com.fiz.tetriswithlife.game.domain.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.math.min

const val widthGrid: Int = 13
const val heightGrid: Int = 25

@HiltViewModel
class GameViewModel @Inject constructor(var recordRepository: RecordRepository) : ViewModel() {
    var gameState: MutableLiveData<GameState> = MutableLiveData(); private set

    private var controller = Controller()
    private var job: Job? = null

    private var prevTime = System.currentTimeMillis()
    private var ending = 1.0
    var running = false

    fun tryLoadState(gameState: GameState?) {
        if (this.gameState.value == null)
            this.gameState.value = gameState
                ?: GameState(widthGrid, heightGrid, recordRepository.loadRecord())
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
        gameState.value?.let {
            val now = System.currentTimeMillis()
            val deltaTime = min(now - prevTime, 100).toInt() / 1000.0
            if (deltaTime == 0.0) return

            if (it.status != "pause") {
                var status = true
                if (ending == 1.0)
                    status = it.update(controller, deltaTime) {
                        if (it.scores > recordRepository.loadRecord()) {
                            it.record = it.scores
                            recordRepository.saveRecord(it.record)
                        }
                    }

                if (!status || ending != 1.0)
                    ending -= deltaTime
            }

            if (ending < 0 || it.status == "new game") {
                it.new(recordRepository.loadRecord())
                ending = 1.0
            }

            gameState.postValue(it)

            prevTime = now
        }
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
            gameState.value?.status = "new game"
        }
    }

    fun clickPauseButton() {
        job?.let {
            gameState.value?.clickPause()
        }
    }

    fun activityStop() {
        running = false
    }
}