package com.fiz.tetriswithlife

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {
    private var gameThread: GameThread? = null
    private lateinit var newGameButton: Button
    private lateinit var pauseButton: Button
    private lateinit var exitButton: Button
    private lateinit var scoresTextView: TextView
    private lateinit var recordTextView: TextView
    private lateinit var infoBreathTextview: TextView
    private lateinit var breathTextview: TextView
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button
    private lateinit var downButton: Button
    private lateinit var rotateButton: Button
    private lateinit var gameSurfaceView: SurfaceView
    private lateinit var nextFigureSurfaceView: SurfaceView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        newGameButton = findViewById(R.id.new_game_game_button)
        pauseButton = findViewById(R.id.pause_game_button)
        exitButton = findViewById(R.id.exit_game_button)
        scoresTextView = findViewById(R.id.scores_game_textview)
        recordTextView = findViewById(R.id.record_game_textview)
        infoBreathTextview = findViewById(R.id.infobreath_game_textview)
        breathTextview = findViewById(R.id.breath_game_textview)
        leftButton = findViewById(R.id.left_game_button)
        rightButton = findViewById(R.id.right_game_button)
        downButton = findViewById(R.id.down_game_button)
        rotateButton = findViewById(R.id.rotate_game_button)

        gameSurfaceView = findViewById(R.id.game_game_surfaceview)
        nextFigureSurfaceView = findViewById(R.id.nextfigure_game_surfaceview)

        gameThread = GameThread(
            gameSurfaceView,
            nextFigureSurfaceView,
            applicationContext,
            scoresTextView,
            recordTextView,
            infoBreathTextview,
            breathTextview,
            pauseButton,
        )
        gameThread!!.setRunning(true)
        gameThread!!.start()

        leftButton.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> gameThread!!.controller
                    .actionLeft()
                MotionEvent.ACTION_MOVE -> {
                    /* for lint */
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> gameThread!!.controller
                    .actionCancel()
            }
            true
        }

        rightButton.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> gameThread!!.controller
                    .actionRight()
                MotionEvent.ACTION_MOVE -> {
                    /* for lint */
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> gameThread!!.controller
                    .actionCancel()
            }
            true
        }

        downButton.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> gameThread!!.controller
                    .actionDown()
                MotionEvent.ACTION_MOVE -> {
                    /* for lint */
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> gameThread!!.controller
                    .actionCancel()
            }
            true
        }

        rotateButton.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> gameThread!!.controller
                    .actionUp()
                MotionEvent.ACTION_MOVE -> {
                    /* for lint */
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> gameThread!!.controller
                    .actionCancel()
            }
            true
        }

        newGameButton.setOnClickListener {
            gameThread!!.state.status = "new game"
        }
        pauseButton.setOnClickListener {
            gameThread!!.state.clickPause()
        }
        exitButton.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        var retry = true
        gameThread!!.setRunning(false)
        while (retry) {
            try {
                gameThread!!.join()
                retry = false
            } catch (e: InterruptedException) {
                /* for lint */
            }
        }
    }


}



