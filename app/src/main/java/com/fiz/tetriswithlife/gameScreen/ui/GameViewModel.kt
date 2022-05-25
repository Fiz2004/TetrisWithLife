package com.fiz.tetriswithlife.gameScreen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.tetriswithlife.gameScreen.data.RecordRepository
import com.fiz.tetriswithlife.gameScreen.domain.models.Controller
import com.fiz.tetriswithlife.gameScreen.domain.models.Game
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

    var game: Game = Game(grid = Grid(widthGrid, heightGrid))

    var viewState: MutableStateFlow<ViewState> =
        MutableStateFlow(
            ViewState(
                gameState = game,
                record = recordRepository.loadRecord()
            )
        )
        private set

    private var gameJob: Job? = null

    fun loadState(viewState: ViewState?) {
        this.viewState.value = viewState ?: return
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
        if (viewState.value.isStatusPause())
            return

        if (viewState.value.isNewGame()) {
            game.newGame()
            viewState.value = viewState.value
                .copy(
                    status = ViewState.Companion.StatusCurrentGame.Playing,
                    gameState = game,
                    timeToRestart = SecTimeForRestartForEndGame,
                    changed = !viewState.value.changed
                )
        }

        val status = when (viewState.value.isGameContinue()) {
            true -> {
                game.updateActors(deltaTime, controller, plusScores = { score: Int ->
                    if (score > recordRepository.loadRecord()) {
                        recordRepository.saveRecord(score)
                        viewState.value =
                            viewState.value
                                .copy(
                                    record = recordRepository.loadRecord(),
                                    changed = !viewState.value.changed
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
            viewState.value.gameEnd(deltaTime)

        viewState.value = viewState.value
            .copy(
                gameState = game,
                changed = !viewState.value.changed
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
        viewState.value = viewState.value
            .copy(status = ViewState.Companion.StatusCurrentGame.NewGame)
    }

    fun clickPauseButton() {
        viewState.value = viewState.value
            .copy(status = viewState.value.getNewStatus())
    }

    companion object {

        enum class StatusUpdateGame {
            Continue, End
        }
    }

}

