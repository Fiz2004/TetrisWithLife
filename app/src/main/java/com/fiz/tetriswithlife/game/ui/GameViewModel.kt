package com.fiz.tetriswithlife.game.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.tetriswithlife.game.data.RecordRepository
import com.fiz.tetriswithlife.game.domain.Controller
import com.fiz.tetriswithlife.game.domain.FormatUseCase
import com.fiz.tetriswithlife.game.domain.UpdateGameStateForTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.math.min

const val widthGrid: Int = 13
const val heightGrid: Int = 25

@HiltViewModel
class GameViewModel @Inject constructor(
    recordRepository: RecordRepository,
    private val updateGameStateForTimeUseCase: UpdateGameStateForTimeUseCase,
    private val formatUseCase: FormatUseCase,
    private var controller: Controller
) : ViewModel() {

    var gameState: MutableStateFlow<GameState> =
        MutableStateFlow(GameState(widthGrid, heightGrid, recordRepository.loadRecord()))
        private set

    var uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState()); private set

    init {
        gameState.onEach {
            uiState.value = formatUseCase(uiState.value, it)
        }.launchIn(viewModelScope)
    }

    private var job: Job? = null

    private var running = false

    fun loadState(gameState: GameState) {
        this.gameState.value = gameState
    }

    fun startGame() {

        viewModelScope.launch(Dispatchers.Default) {
            job?.cancelAndJoin()
            job = viewModelScope.launch(Dispatchers.Default, block = gameLoop())
        }

    }

    private fun gameLoop(): suspend CoroutineScope.() -> Unit = {
        running = true
        var prevTime = System.currentTimeMillis()

        while (isActive) {
            if (running) {

                val now = System.currentTimeMillis()
                val deltaTime = min(now - prevTime, 100).toInt() / 1000.0
                if (deltaTime == 0.0) continue

                gameState.value = updateGameStateForTimeUseCase(
                    gameState.value,
                    deltaTime,
                    controller
                ).copy(changed = !gameState.value.changed)

                prevTime = now
            }
        }
    }

    fun clickLeftButton() {
        controller = Controller(left = true)
    }

    fun clickRightButton() {
        controller = Controller(right = true)
    }

    fun clickDownButton() {
        controller = Controller(down = true)
    }

    fun clickRotateButton() {
        controller = Controller(up = true)
    }

    fun clickCancel() {
        controller = Controller()
    }

    fun clickNewGameButton() {
        gameState.value.status = GameState.Companion.StatusCurrentGame.NewGame
    }

    fun clickPauseButton() {
        gameState.value.clickPause()
    }

    fun activityStop() {
        running = false
    }
}

