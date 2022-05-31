package com.fiz.tetriswithlife.gameScreen.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.use
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.fiz.tetriswithlife.R
import com.fiz.tetriswithlife.databinding.ActivityGameBinding
import com.fiz.tetriswithlife.gameScreen.game.Game
import com.fiz.tetriswithlife.util.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class GameActivity : AppCompatActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    private val binding: ActivityGameBinding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var display: Display

    private var colorBackground: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        colorBackground = obtainStyledAttributes(
            intArrayOf(R.attr.backgroundColor)
        ).use {
            it.getColor(0, Color.MAGENTA)
        }

        val loadGame =
            savedInstanceState?.getSerializable(GAME) as? Game
        val loadStateStatus =
            savedInstanceState?.getSerializable(STATE_STATUS) as? ViewState.Companion.StatusCurrentGame
        gameViewModel.loadGame(loadGame, loadStateStatus)

        binding.gameSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {}

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                gameViewModel.gameSurfaceReady(
                    width = binding.gameSurfaceView.width,
                    height = binding.gameSurfaceView.height
                )
            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {}

        })

        binding.nextFigureSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {}

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                gameViewModel.nextFigureReady(
                    width = binding.nextFigureSurfaceView.width,
                    height = binding.nextFigureSurfaceView.height
                )
            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {}

        })

        bindListener()


        var lastTime = System.currentTimeMillis()
        var deltaTime = 60

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                withContext(Dispatchers.Main) {
                    gameViewModel.viewState.collectLatest { gameState ->

                        val now = System.currentTimeMillis()
                        deltaTime = ((deltaTime + now - lastTime) / 2).toInt()

                        Log.d("FPS", (1000 / deltaTime).toString())

                        binding.gameSurfaceView.holder.lockCanvas()?.let {
                            display.render(gameState, it, colorBackground)
                            binding.gameSurfaceView.holder.unlockCanvasAndPost(it)
                        }

                        binding.nextFigureSurfaceView.holder.lockCanvas()?.let {
                            display.renderInfo(gameState, it, colorBackground)
                            binding.nextFigureSurfaceView.holder.unlockCanvasAndPost(it)
                        }

                        binding.scoresTextView.text = getString(
                            R.string.scores_game_textview, gameState.textForScores
                        )

                        binding.recordTextview.text =
                            getString(
                                R.string.record_game_textview,
                                gameState.textForRecord
                            )

                        binding.pauseButton.text =
                            getString(gameState.textResourceForPauseResumeButton)

                        binding.infoBreathTextView.setVisible(gameState.visibilityForInfoBreathTextView)

                        binding.infoBreathTextView.setTextColor(gameState.colorForInfoBreathTextView)
                        binding.infoBreathTextView.text = getString(
                            R.string.infobreath_game_textview,
                            gameState.textForInfoBreathTextView
                        )

                        lastTime = now
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bindListener() {
        this.binding.leftButton.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> gameViewModel.clickLeftButton(true)
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> gameViewModel.clickLeftButton(false)
            }
            true
        }

        binding.rightButton.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> gameViewModel.clickRightButton(true)
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> gameViewModel.clickRightButton(false)
            }
            true
        }

        binding.downButton.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> gameViewModel.clickDownButton(true)
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> gameViewModel.clickDownButton(false)
            }
            true
        }

        binding.rotateButton.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> gameViewModel.clickRotateButton(true)
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> gameViewModel.clickRotateButton(false)
            }
            true
        }

        binding.newGameButton.setOnClickListener {
            gameViewModel.clickNewGameButton()
        }
        binding.pauseButton.setOnClickListener {
            gameViewModel.clickPauseButton()
        }
        binding.exitButton.setOnClickListener {
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        gameViewModel.stopGame()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(GAME, gameViewModel.game)
        outState.putSerializable(STATE_STATUS, gameViewModel.viewState.value.status)
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val GAME = "game"
        const val STATE_STATUS = "state"
    }

}
