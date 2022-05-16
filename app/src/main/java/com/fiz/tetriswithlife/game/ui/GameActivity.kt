package com.fiz.tetriswithlife.game.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.fiz.tetriswithlife.R
import com.fiz.tetriswithlife.databinding.ActivityGameBinding
import com.fiz.tetriswithlife.game.data.RecordRepository
import com.fiz.tetriswithlife.game.domain.Controller
import com.fiz.tetriswithlife.game.domain.Display
import com.fiz.tetriswithlife.game.domain.GameLoop
import com.fiz.tetriswithlife.game.domain.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

private const val widthCanvas: Int = 13
private const val heightCanvas: Int = 25

@AndroidEntryPoint
class GameActivity : AppCompatActivity(), Display.Companion.Listener {
    private var gameLoop: GameLoop? = null
    private var job: Job? = null
    private val surfaceReady = mutableListOf(false, false)

    @Inject
    lateinit var recordRepository: RecordRepository
    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val loadState = savedInstanceState?.getSerializable("state")
        val state: State = if (loadState != null)
            loadState as State
        else
            State(
                widthCanvas, heightCanvas, recordRepository
            )
        val display = Display(
            binding.gameSurfaceView,
            this
        )

        binding.gameSurfaceView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            surfaceReady[0] = true
            canStartGame(state, display)
        }

        binding.nextFigureSurfaceView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            surfaceReady[1] = true
            canStartGame(state, display)
        }

        bindListener()
    }

    private fun canStartGame(state: State, display: Display) {
        if (surfaceReady.all { it })
            job = CoroutineScope(Dispatchers.Default).launch {
                gameLoop = GameLoop(
                    state,
                    display,
                    Controller(),
                    binding.gameSurfaceView,
                    binding.nextFigureSurfaceView
                )
                gameLoop?.setRunning(true)
                gameLoop?.run()
            }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bindListener() {
        binding.leftButton.setOnTouchListener { _: View, event: MotionEvent ->
            gameLoop?.let {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> it.controller
                        .actionLeft()
                    MotionEvent.ACTION_MOVE -> {
                        /* for lint */
                    }
                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> it.controller
                        .actionCancel()
                }
            }
            true
        }

        binding.rightButton.setOnTouchListener { _: View, event: MotionEvent ->
            gameLoop?.let {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> it.controller
                        .actionRight()
                    MotionEvent.ACTION_MOVE -> {
                        /* for lint */
                    }
                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> it.controller
                        .actionCancel()
                }
            }
            true
        }

        binding.downButton.setOnTouchListener { _: View, event: MotionEvent ->
            gameLoop?.let {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> it.controller
                        .actionDown()
                    MotionEvent.ACTION_MOVE -> {
                        /* for lint */
                    }
                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> it.controller
                        .actionCancel()
                }
            }
            true
        }

        binding.rotateButton.setOnTouchListener { _: View, event: MotionEvent ->
            gameLoop?.let {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> it.controller
                        .actionUp()
                    MotionEvent.ACTION_MOVE -> {
                        /* for lint */
                    }
                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> it.controller
                        .actionCancel()
                }
            }
            true
        }

        binding.newGameButton.setOnClickListener {
            gameLoop?.state?.status = "new game"
        }
        binding.pauseButton.setOnClickListener {
            gameLoop?.state?.clickPause()
        }
        binding.exitButton.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        var retry = true
        gameLoop?.setRunning(false)
        while (retry) {
            try {
                runBlocking {
                    job?.cancelAndJoin()
                    job=null
                }
                retry = false
            } catch (e: InterruptedException) {
                /* for lint */
            }
        }
    }

    override fun setScoresTextView(scores: String) {
        binding.scoresTextView.text = resources.getString(
            R.string.scores_game_textview, scores.padStart
                (6, '0')
        )
    }

    override fun setRecordTextView(record: String) {
        binding.recordTextview.text =
            resources.getString(R.string.record_game_textview, record.padStart(6, '0'))
    }

    override fun pauseButtonClick(status: String) {
        binding.pauseButton.text = if (status == "pause")
            resources.getString(R.string.resume_game_button)
        else
            resources.getString(R.string.pause_game_button)
    }

    override fun infoBreathTextviewChangeVisibility(visibility: Boolean) {
        if (!visibility) {
            if (!binding.breathTextView.isVisible) {
                binding.infoBreathTextView.post {
                    binding.infoBreathTextView.visibility = View.VISIBLE
                }
            }
        } else if (binding.breathTextView.isVisible) {
            binding.infoBreathTextView.post {
                binding.infoBreathTextView.visibility = View.INVISIBLE
            }
        }
    }

    override fun breathTextviewChangeVisibilityAndColor(
        visibility: Boolean, sec: Double, color: Int
    ) {
        if (!visibility) {
            if (!binding.breathTextView.isVisible) {
                binding.breathTextView.post { binding.breathTextView.visibility = View.VISIBLE }
            }
            binding.breathTextView.post { binding.breathTextView.text = sec.toInt().toString() }
        } else if (binding.breathTextView.isVisible) {
            binding.breathTextView.post { binding.breathTextView.visibility = View.INVISIBLE }
        }

        binding.breathTextView.post {
            binding.breathTextView.setTextColor(
                Color.rgb(
                    255,
                    color,
                    color
                )
            )
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        gameLoop?.let {
            outState.putSerializable("state", it.state)
        }
    }

}



