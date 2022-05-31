package com.fiz.tetriswithlife.gameScreen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.tetriswithlife.gameScreen.data.BitmapRepository
import com.fiz.tetriswithlife.gameScreen.data.RecordRepository
import com.fiz.tetriswithlife.gameScreen.domain.RefreshGameStateFromGame
import com.fiz.tetriswithlife.gameScreen.domain.models.Controller
import com.fiz.tetriswithlife.gameScreen.game.Game
import com.fiz.tetriswithlife.gameScreen.game.Grid
import com.fiz.tetriswithlife.gameScreen.game.Vector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlin.math.min

const val widthGrid: Int = 13
const val heightGrid: Int = 25

private const val NUMBER_COLUMNS_IMAGES_FON = 4
private const val NUMBER_ROWS_IMAGES_FON = 4

private const val mSecFromFPS60 = ((1.0 / 60.0) * 1000.0).toLong()


@HiltViewModel
class GameViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private var controller: Controller
) : ViewModel() {

    private var refreshGameStateFromGame: RefreshGameStateFromGame = RefreshGameStateFromGame()

    var game: Game = Game(grid = Grid(widthGrid, heightGrid))

    var viewState: MutableStateFlow<ViewState> =
        MutableStateFlow(
            ViewState(
                gameState = refreshGameStateFromGame(game),
                record = recordRepository.loadRecord()
            )
        )
        private set

    private var gameJob: Job? = null

    private var surfaceReady = mutableListOf(false, false)

    @Inject
    lateinit var bitmapRepository: BitmapRepository

    fun loadGame(game: Game?, loadStateStatus: ViewState.Companion.StatusCurrentGame?) {
        this.game = game ?: return
        viewState.value = viewState.value
            .copy(
                status = loadStateStatus ?: return
            )
    }

    private fun startGame(refreshGameStateFromGame: RefreshGameStateFromGame) {
        this.refreshGameStateFromGame = refreshGameStateFromGame
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
                    gameState = refreshGameStateFromGame(game),
                    status = ViewState.Companion.StatusCurrentGame.Playing,
                    timeToRestart = SecTimeForRestartForEndGame,
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
                                    gameState = refreshGameStateFromGame(game),
                                    record = recordRepository.loadRecord(),
                                )

                    }
                })
            }
            false -> {
                StatusUpdateGame.End
            }
        }

        if (status == StatusUpdateGame.End) {
            val newTimeToRestart = viewState.value.timeToRestart - deltaTime
            viewState.value = viewState.value
                .copy(
                    timeToRestart = newTimeToRestart,
                )
        }

        viewState.value = viewState.value
            .copy(
                gameState = refreshGameStateFromGame(game),
            )
    }

    fun stopGame() {
        surfaceReady = mutableListOf(false, false)
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
            .copy(
                gameState = refreshGameStateFromGame(game),
                status = ViewState.Companion.StatusCurrentGame.NewGame
            )
    }

    fun clickPauseButton() {
        viewState.value = viewState.value
            .copy(
                gameState = refreshGameStateFromGame(game),
                status = viewState.value.getNewStatus()
            )
    }

    fun gameSurfaceReady(width: Int, height: Int) {

        refreshGameStateFromGame.tile =
            bitmapRepository.bmpFon.width / NUMBER_COLUMNS_IMAGES_FON

        refreshGameStateFromGame.newTile = min(
            height / heightGrid,
            width / widthGrid
        ).toFloat()

        refreshGameStateFromGame.offset = Vector(
            ((width - widthGrid * refreshGameStateFromGame.newTile) / 2).toInt(),
            ((height - heightGrid * refreshGameStateFromGame.newTile) / 2).toInt()
        )

        surfaceReady[0] = true
        if (surfaceReady.all { it }) {
            startGame(refreshGameStateFromGame)
        }
    }

    fun nextFigureReady(width: Int, height: Int) {

        refreshGameStateFromGame.oneTileInfo = min(
            width / 4,
            height / 4
        ).toFloat()

        surfaceReady[1] = true
        if (surfaceReady.all { it }) {
            startGame(refreshGameStateFromGame)
        }
    }

    companion object {

        enum class StatusUpdateGame {
            Continue, End
        }
    }

}

