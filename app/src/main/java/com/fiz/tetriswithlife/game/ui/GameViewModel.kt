package com.fiz.tetriswithlife.game.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.tetriswithlife.game.data.RecordRepository
import com.fiz.tetriswithlife.game.domain.Controller
import com.fiz.tetriswithlife.game.domain.FormatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.math.min

const val widthGrid: Int = 13
const val heightGrid: Int = 25

private const val SecTimeForRestartForEndGame = 1.0

@HiltViewModel
class GameViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val formatUseCase: FormatUseCase,
    private var controller: Controller
) : ViewModel() {

    var gameState: MutableStateFlow<GameState> =
        MutableStateFlow(GameState(widthGrid, heightGrid, recordRepository.loadRecord()))
        private set

    var uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState()); private set

    init {
        gameState.onEach {
            uiState.value = uiState.value.copy(
                scores = formatUseCase.getScore(it.scores),
                record = formatUseCase.getRecord(it.record),
                pauseResumeButton = formatUseCase.getTextForPauseResumeButton(it.status),
                infoBreathTextViewVisibility = formatUseCase.getVisibilityForInfoBreathTextView(it.character.breath),
                textForBreathTextView = formatUseCase.getTextForBreathTextView(
                    it.character.breath,
                    it.character.timeBreath
                ),
                colorForBreathTextView = formatUseCase.getColorForBreathTextView(
                    it.character.breath,
                    it.character.timeBreath
                ),
            )
        }.launchIn(viewModelScope)
    }

    private var job: Job? = null

    private var timeToRestart = SecTimeForRestartForEndGame
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

                stateUpdate(deltaTime)
                gameState.value = gameState.value.copy(changed = !gameState.value.changed)

                prevTime = now
            }
        }
    }

    private fun stateUpdate(deltaTime: Double) {
        if (gameState.value.status == GameState.Companion.StatusCurrentGame.Pause) {
            return
        }

        if (timeToRestart < 0 || gameState.value.status == GameState.Companion.StatusCurrentGame.NewGame) {
            gameState.value = GameState(widthGrid, heightGrid, recordRepository.loadRecord())
            timeToRestart = SecTimeForRestartForEndGame
            return
        }

        val status = if (timeToRestart == SecTimeForRestartForEndGame)
            gameState.value.update(controller, deltaTime) { score ->

                if (score > recordRepository.loadRecord())
                    recordRepository.saveRecord(score)

            }
        else
            GameState.Companion.StatusUpdateGame.End

        if (status == GameState.Companion.StatusUpdateGame.End)
            timeToRestart -= deltaTime

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