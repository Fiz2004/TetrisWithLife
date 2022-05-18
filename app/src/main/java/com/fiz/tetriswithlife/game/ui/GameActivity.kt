package com.fiz.tetriswithlife.game.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.fiz.tetriswithlife.R
import com.fiz.tetriswithlife.databinding.ActivityGameBinding
import com.fiz.tetriswithlife.game.data.RecordRepository
import com.fiz.tetriswithlife.game.domain.Controller
import com.fiz.tetriswithlife.game.domain.Display
import com.fiz.tetriswithlife.game.domain.GameState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.math.min

private const val widthCanvas: Int = 13
private const val heightCanvas: Int = 25

@AndroidEntryPoint
class GameActivity : AppCompatActivity(), Display.Companion.Listener {
    private val gameViewModel: GameViewModel by viewModels()
    private lateinit var gameState: GameState
    private val controller = Controller()
    private var job: Job? = null
    private val surfaceReady = mutableListOf(false, false)

    @Inject
    lateinit var recordRepository: RecordRepository
    private lateinit var binding: ActivityGameBinding
    private lateinit var display: Display

    private var prevTime = System.currentTimeMillis()
    private var ending = 1.0
    var running = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gameState = savedInstanceState?.getSerializable(STATE) as? GameState
            ?: GameState(widthCanvas, heightCanvas, recordRepository.loadRecord())

        binding.gameSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {}

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                surfaceReady[0] = true
                display = Display(
                    binding.gameSurfaceView,
                    this@GameActivity,
                    gameState
                )
                if (surfaceReady.all { it })
                    canStartGame()
            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {}

        })

        binding.nextFigureSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {}

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                surfaceReady[1] = true
                if (surfaceReady.all { it })
                    canStartGame()
            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {}

        })

        bindListener()
    }

    private fun canStartGame() {
        job = CoroutineScope(Dispatchers.Default).launch {
            running = true

            while (isActive) {
                if (running) {

                    Log.d("Game", "+")

                    stateUpdate()

                    displayUpdate()

                }
            }
        }
    }


    private fun stateUpdate() {
        val now = System.currentTimeMillis()
        val deltaTime = min(now - prevTime, 100).toInt() / 1000.0

        if (gameState.status != "pause") {
            var status = true
            if (ending == 1.0)
                status = gameState.update(controller, deltaTime) {
                    if (gameState.scores > recordRepository.loadRecord()) {
                        gameState.record = gameState.scores
                        recordRepository.saveRecord(gameState.record)
                    }
                }

            if (!status || ending != 1.0)
                ending -= deltaTime
        }

        if (ending < 0 || gameState.status == "new game") {
            gameState.new(recordRepository.loadRecord())
            ending = 1.0
        }

        prevTime = now
    }

    private fun displayUpdate() {

        binding.gameSurfaceView.holder.lockCanvas(null)?.let {
            display.render(gameState, it)
            binding.gameSurfaceView.holder.unlockCanvasAndPost(it)
        }

        binding.nextFigureSurfaceView.holder.lockCanvas(null)?.let {
            display.renderInfo(gameState, it)
            binding.nextFigureSurfaceView.holder.unlockCanvasAndPost(it)
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bindListener() {
        binding.leftButton.setOnTouchListener { _: View, event: MotionEvent ->
            job?.let {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> controller
                        .actionLeft()
                    MotionEvent.ACTION_MOVE -> {
                        /* for lint */
                    }
                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> controller
                        .actionCancel()
                }
            }
            true
        }

        binding.rightButton.setOnTouchListener { _: View, event: MotionEvent ->
            job?.let {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> controller
                        .actionRight()
                    MotionEvent.ACTION_MOVE -> {
                        /* for lint */
                    }
                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> controller
                        .actionCancel()
                }
            }
            true
        }

        binding.downButton.setOnTouchListener { _: View, event: MotionEvent ->
            job?.let {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> controller
                        .actionDown()
                    MotionEvent.ACTION_MOVE -> {
                        /* for lint */
                    }
                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> controller
                        .actionCancel()
                }
            }
            true
        }

        binding.rotateButton.setOnTouchListener { _: View, event: MotionEvent ->
            job?.let {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> controller
                        .actionUp()
                    MotionEvent.ACTION_MOVE -> {
                        /* for lint */
                    }
                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> controller
                        .actionCancel()
                }
            }
            true
        }

        binding.newGameButton.setOnClickListener {
            job?.let {
                gameState.status = "new game"
            }
        }
        binding.pauseButton.setOnClickListener {
            job?.let {
                gameState.clickPause()
            }
        }
        binding.exitButton.setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        running = true
    }

    override fun onStop() {
        super.onStop()
        running = false
    }

    override fun onDestroy() {
        super.onDestroy()
        runBlocking {
            job?.cancelAndJoin()
            job = null
        }
    }

    override fun setScoresTextView(scores: String) {
        binding.scoresTextView.post {
            binding.scoresTextView.text = resources.getString(
                R.string.scores_game_textview, scores.padStart
                    (6, '0')
            )
        }
    }

    override fun setRecordTextView(record: String) {
        binding.recordTextview.post {
            binding.recordTextview.text =
                resources.getString(R.string.record_game_textview, record.padStart(6, '0'))
        }
    }

    override fun pauseButtonClick(status: String) {
        binding.pauseButton.post {
            binding.pauseButton.text = if (status == "pause")
                resources.getString(R.string.resume_game_button)
            else
                resources.getString(R.string.pause_game_button)
        }
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

    companion object {
        const val STATE = "state"
    }

}



