package com.fiz.tetriswithlife.game.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.fiz.tetriswithlife.R
import com.fiz.tetriswithlife.databinding.ActivityGameBinding
import com.fiz.tetriswithlife.game.data.BitmapRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GameActivity : AppCompatActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    private var surfaceReady = mutableListOf(false, false)

    private lateinit var binding: ActivityGameBinding

    private lateinit var display: Display

    @Inject
    lateinit var bitmapRepository: BitmapRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        (savedInstanceState?.getSerializable(STATE) as? GameState)?.let {
            gameViewModel.loadState(it)
        }

        binding.gameSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {}

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                display = Display(
                    binding.gameSurfaceView.width,
                    binding.gameSurfaceView.height,
                    widthGrid,
                    heightGrid,
                    bitmapRepository
                )

                surfaceReady[0] = true
                if (surfaceReady.all { it })
                    gameViewModel.startGame()
            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {}

        })

        binding.nextFigureSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {}

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                surfaceReady[1] = true
                if (surfaceReady.all { it })
                    gameViewModel.startGame()
            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {}

        })

        bindListener()

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    gameViewModel.gameState.collectLatest { gameState ->

                        binding.gameSurfaceView.holder.lockCanvas(null)?.let {
                            display.render(gameState, it)
                            binding.gameSurfaceView.holder.unlockCanvasAndPost(it)
                        }

                        binding.nextFigureSurfaceView.holder.lockCanvas(null)?.let {
                            display.renderInfo(gameState, it)
                            binding.nextFigureSurfaceView.holder.unlockCanvasAndPost(it)
                        }

                    }
                }

                launch {

                    gameViewModel.uiState.collectLatest { uiState ->
                        binding.scoresTextView.text = resources.getString(
                            R.string.scores_game_textview, uiState.scores
                        )

                        binding.recordTextview.text =
                            resources.getString(R.string.record_game_textview, uiState.record)

                        binding.pauseButton.text = resources.getString(uiState.pauseResumeButton)

                        val visibility = if (uiState.infoBreathTextViewVisibility)
                            View.VISIBLE
                        else
                            View.INVISIBLE

                        binding.infoBreathTextView.visibility = visibility
                        binding.breathTextView.visibility = visibility

                        binding.breathTextView.setTextColor(uiState.colorForBreathTextView)
                        binding.breathTextView.text = uiState.textForBreathTextView
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bindListener() {
        this.binding.leftButton.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> gameViewModel.clickLeftButton()
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> gameViewModel.clickCancel()
            }
            true
        }

        binding.rightButton.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> gameViewModel.clickRightButton()
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> gameViewModel.clickCancel()
            }
            true
        }

        binding.downButton.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> gameViewModel.clickDownButton()
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> gameViewModel.clickCancel()
            }
            true
        }

        binding.rotateButton.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> gameViewModel.clickRotateButton()
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> gameViewModel.clickCancel()
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
        surfaceReady = mutableListOf(false, false)
        gameViewModel.activityStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        gameViewModel.gameState.value.let {
            outState.putSerializable(STATE, it)
        }
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val STATE = "state"
    }

}



