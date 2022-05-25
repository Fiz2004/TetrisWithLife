package com.fiz.tetriswithlife.gameScreen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.tetriswithlife.gameScreen.data.RecordRepository
import com.fiz.tetriswithlife.gameScreen.domain.models.Controller
import com.fiz.tetriswithlife.gameScreen.domain.models.Grid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlin.math.min

const val widthGrid: Int = 13
const val heightGrid: Int = 25

private const val mSecFromFPS60=((1.0 / 60.0) * 1000.0).toLong()

@HiltViewModel
class GameViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private var controller: Controller
) : ViewModel() {

    var grid: Grid = Grid(widthGrid, heightGrid)

    var gameState: MutableStateFlow<GameState> =
        MutableStateFlow(
            GameState(
                gridState = GridState.fromGrid(grid),
                record = recordRepository.loadRecord()
            )
        )
        private set

    private var gameJob: Job? = null

    fun loadState(gameState: GameState?) {
        this.gameState.value = gameState ?: return
    }

    fun startGame() {
        viewModelScope.launch(Dispatchers.Default) {
            gameJob = viewModelScope.launch(Dispatchers.Default, block = gameLoop())
        }
    }

    private fun gameLoop(): suspend CoroutineScope.() -> Unit = {
        var lastTime = System.currentTimeMillis()

        while (isActive) {

            val now = System.currentTimeMillis()
            val deltaTime = min(now - lastTime, mSecFromFPS60) / 1000.0
            if (deltaTime == 0.0) continue

            update(deltaTime)

            lastTime = now
        }
    }

    private fun update(deltaTime: Double) {
        if (gameState.value.isStatusPause())
            return

        if (gameState.value.isNewGame()) {
            grid.newGame()
            gameState.value = gameState.value
                .copy(
                    gridState = GridState.fromGrid(grid),
                    timeToRestart = SecTimeForRestartForEndGame,
                    changed = !gameState.value.changed
                )
        }

        val status = when (gameState.value.isGameContinue()) {
            true -> {
                grid.updateActors(deltaTime, controller, plusScores = { score: Int ->
                    if (score > recordRepository.loadRecord()) {
                        recordRepository.saveRecord(score)
                        gameState.value =
                            gameState.value
                                .copy(
                                    record = recordRepository.loadRecord(),
                                    changed = !gameState.value.changed
                                )

                    }
                })
                StatusUpdateGame.Continue
            }
            false -> {
                StatusUpdateGame.End
            }
        }

        if (status == StatusUpdateGame.End)
            gameState.value.gameEnd(deltaTime)

        gameState.value = gameState.value
            .copy(
                gridState = GridState.fromGrid(grid),
                changed = !gameState.value.changed
            )
    }

    fun stopGame() {
        viewModelScope.launch(Dispatchers.Default) {
            gameJob?.cancelAndJoin()
        }
    }


    fun clickLeftButton(value: Boolean) {
        controller = controller.copy(left = value)
    }

    fun clickRightButton(value: Boolean) {
        controller = controller.copy(right = value)
    }

    fun clickDownButton(value: Boolean) {
        controller = controller.copy(down = value)
    }

    fun clickRotateButton(value: Boolean) {
        controller = controller.copy(up = value)
    }

    fun clickNewGameButton() {
        gameState.value = gameState.value
            .copy(status = GameState.Companion.StatusCurrentGame.NewGame)
    }

    fun clickPauseButton() {
        gameState.value = gameState.value
            .copy(status = gameState.value.getNewStatus())
    }

    companion object {

        enum class StatusUpdateGame {
            Continue, End
        }
    }

}

