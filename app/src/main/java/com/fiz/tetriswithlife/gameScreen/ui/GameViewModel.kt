package com.fiz.tetriswithlife.gameScreen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.tetriswithlife.gameScreen.domain.GetGameStateFromGame
import com.fiz.tetriswithlife.gameScreen.domain.models.Controller
import com.fiz.tetriswithlife.gameScreen.game.Game
import com.fiz.tetriswithlife.gameScreen.ui.models.GameForSaveInstanceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    var game: Game,
    private val getGameStateFromGame: GetGameStateFromGame,
    private val controller: Controller
) : ViewModel() {

    var gameState: MutableStateFlow<GameState> =
        MutableStateFlow(getGameStateFromGame(game))
        private set

    private var gameJob: Job? = null

    private var surfaceReady = mutableListOf(false, false)

    fun loadGame(gameForSaveInstanceState: GameForSaveInstanceState?) {
        game = gameForSaveInstanceState?.toGame(game) ?: return
    }

    fun gameSurfaceReady(width: Int, height: Int) {

        getGameStateFromGame.initGameSurface(width, height)

        surfaceReady[0] = true
        if (surfaceReady.all { it }) {
            startGame()
        }
    }

    fun nextFigureSurfaceReady(width: Int, height: Int) {

        getGameStateFromGame.initNextFigureSurface(width, height)

        surfaceReady[1] = true
        if (surfaceReady.all { it }) {
            startGame()
        }
    }

    private fun startGame() {
        viewModelScope.launch(Dispatchers.Default) {
            gameJob?.cancelAndJoin()
            gameJob = viewModelScope.launch(Dispatchers.Default) {
                while (isActive) {

                    game.update(controller)

                    gameState.value = getGameStateFromGame(game)
                }
            }
        }
    }

    fun stopGame() {
        surfaceReady = mutableListOf(false, false)
        viewModelScope.launch(Dispatchers.Default) {
            gameJob?.cancelAndJoin()
        }
    }

    fun clickNewGameButton() {
        game.clickNewGame()
    }

    fun clickPauseButton() {
        game.clickPause()
    }


    fun clickLeftButton(value: Boolean) {
        controller.left = value
    }

    fun clickRightButton(value: Boolean) {
        controller.right = value
    }

    fun clickDownButton(value: Boolean) {
        controller.down = value
    }

    fun clickRotateButton(value: Boolean) {
        controller.up = value
    }
}

