package com.fiz.tetriswithlife.gameScreen.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fiz.tetriswithlife.R
import com.fiz.tetriswithlife.databinding.ActivityGameBinding
import com.fiz.tetriswithlife.gameScreen.ui.models.GameForSaveInstanceState
import com.fiz.tetriswithlife.util.launchAndRepeatWithViewLifecycleWithMain
import com.fiz.tetriswithlife.util.setVisible
import com.fiz.tetriswithlife.util.themeColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class GameActivity : AppCompatActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    private val binding: ActivityGameBinding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

    private val colorBackground: Int by lazy { themeColor(R.attr.backgroundColor) }

    @Inject
    lateinit var display: Display

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null)
            gameViewModel.clickNewGameButton()

        init(savedInstanceState)
        bindListener()
        collected()
    }

    private fun init(savedInstanceState: Bundle?) {
        val loadGame =
            savedInstanceState?.getSerializable(GameForSaveInstanceState::class.java.simpleName) as? GameForSaveInstanceState
        gameViewModel.loadGame(loadGame)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bindListener() {
        binding.gameSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                gameViewModel.gameSurfaceReady(
                    width = binding.gameSurfaceView.width,
                    height = binding.gameSurfaceView.height
                )
            }

            override fun surfaceCreated(p0: SurfaceHolder) {}
            override fun surfaceDestroyed(p0: SurfaceHolder) {}
        })

        binding.nextFigureSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                gameViewModel.nextFigureSurfaceReady(
                    width = binding.nextFigureSurfaceView.width,
                    height = binding.nextFigureSurfaceView.height
                )
            }

            override fun surfaceCreated(p0: SurfaceHolder) {}
            override fun surfaceDestroyed(p0: SurfaceHolder) {}
        })

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

    private fun collected() {
        val measureFPS = MeasureFPS()

        launchAndRepeatWithViewLifecycleWithMain {
            gameViewModel.gameState.collectLatest { gameState ->

                measureFPS {

                    binding.apply {
                        gameSurfaceView.holder.lockCanvas()?.let {
                            display.render(gameState, it, colorBackground)
                            gameSurfaceView.holder.unlockCanvasAndPost(it)
                        }

                        nextFigureSurfaceView.holder.lockCanvas()?.let {
                            display.renderInfo(gameState, it, colorBackground)
                            nextFigureSurfaceView.holder.unlockCanvasAndPost(it)
                        }

                        scoresTextView.text = getString(
                            R.string.scores_game_textview, gameState.textForScores
                        )

                        recordTextview.text =
                            getString(
                                R.string.record_game_textview,
                                gameState.textForRecord
                            )

                        pauseButton.text =
                            getString(gameState.textResourceForPauseResumeButton)

                        infoBreathTextView.apply {
                            setVisible(gameState.visibilityForInfoBreathTextView)
                            setTextColor(gameState.colorForInfoBreathTextView)
                            text = getString(
                                R.string.infobreath_game_textview,
                                gameState.textForInfoBreathTextView
                            )
                        }
                    }

                }
            }
        }

        launchAndRepeatWithViewLifecycleWithMain {
            gameViewModel.gameEffect.collect {
                if (it is GameEffect.ShowAlertDialog) {
                    AlertDialog.Builder(this@GameActivity)
                        .setTitle(getString(R.string.game_finish))
                        .setMessage(getString(R.string.game_your_scores, it.scores.toString()))
                        .setPositiveButton(getString(R.string.game_alert_ok)) { dialog, _ ->
                            dialog.dismiss()
                            gameViewModel.continueGame()
                        }
                        .setNegativeButton(getString(R.string.game_alert_cancel)) { dialog, _ ->
                            dialog.dismiss()
                            finish()
                        }
                        .create()
                        .show()
                    gameViewModel.showedDialog()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        gameViewModel.stopGame()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(
            GameForSaveInstanceState::class.java.simpleName,
            GameForSaveInstanceState.fromGame(gameViewModel.game)
        )
        super.onSaveInstanceState(outState)
    }

}

