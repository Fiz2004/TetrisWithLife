package com.fiz.tetriswithlife.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.fiz.tetriswithlife.*
import com.fiz.tetriswithlife.databinding.ActivityGameBinding
import kotlinx.coroutines.*

private const val widthCanvas: Int = 13
private const val heightCanvas: Int = 25

class GameActivity : AppCompatActivity(), Display.Companion.Listener {
    private lateinit var binding: ActivityGameBinding
    private var gameScope: GameScope? = null
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val load = savedInstanceState?.getSerializable("state")
        val state: State = if (load != null)
            load as State
        else
            State(
                widthCanvas, heightCanvas, getSharedPreferences("data", Context.MODE_PRIVATE)
            )
        val display = Display(
            binding.gameSurfaceView,
            this
        )

        job = CoroutineScope(Dispatchers.Default).launch {
            gameScope = GameScope(
                state,
                display,
                Controller(),
                binding.gameSurfaceView,
                binding.nextFigureSurfaceView
            )
            gameScope?.setRunning(true)
            gameScope?.run()
        }

        bindListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bindListener() {
        binding.leftButton.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> gameScope!!.controller
                    .actionLeft()
                MotionEvent.ACTION_MOVE -> {
                    /* for lint */
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> gameScope!!.controller
                    .actionCancel()
            }
            true
        }

        binding.rightButton.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> gameScope!!.controller
                    .actionRight()
                MotionEvent.ACTION_MOVE -> {
                    /* for lint */
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> gameScope!!.controller
                    .actionCancel()
            }
            true
        }

        binding.downButton.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> gameScope!!.controller
                    .actionDown()
                MotionEvent.ACTION_MOVE -> {
                    /* for lint */
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> gameScope!!.controller
                    .actionCancel()
            }
            true
        }

        binding.rotateButton.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> gameScope!!.controller
                    .actionUp()
                MotionEvent.ACTION_MOVE -> {
                    /* for lint */
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> gameScope!!.controller
                    .actionCancel()
            }
            true
        }

        binding.newGameButton.setOnClickListener {
            gameScope!!.state.status = "new game"
        }
        binding.pauseButton.setOnClickListener {
            gameScope!!.state.clickPause()
        }
        binding.exitButton.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        var retry = true
        gameScope!!.setRunning(false)
        while (retry) {
            try {
                runBlocking {
                    job?.cancelAndJoin()
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
        if (status == "pause")
            binding.pauseButton.text = resources.getString(R.string.resume_game_button)
        else
            binding.pauseButton.text = resources.getString(R.string.pause_game_button)
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
            binding.breathTextView.setBackgroundColor(
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
        outState.putSerializable("state", gameScope!!.state)
    }

}



