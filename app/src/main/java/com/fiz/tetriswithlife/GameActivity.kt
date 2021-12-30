package com.fiz.tetriswithlife

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {
    private var drawThread: DrawThread? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        newGameButton=findViewById(R.id.new_game_game_button)
        pauseButton=findViewById(R.id.pause_game_button)
        exitButton=findViewById(R.id.exit_game_button)
        scoresTextView=findViewById(R.id.scores_game_textview)
        recordTextView=findViewById(R.id.record_game_textview)
        infoBreathTextview=findViewById(R.id.infobreath_game_textview)
        breathTextview=findViewById(R.id.breath_game_textview)
        leftButton=findViewById(R.id.left_game_button)
        rightButton=findViewById(R.id.right_game_button)
        downButton=findViewById(R.id.down_game_button)
        rotateButton=findViewById(R.id.rotate_game_button)

        gameSurfaceView=findViewById(R.id.game_game_surfaceview)
        nextFigureSurfaceView=findViewById(R.id.nextfigure_game_surfaceview)

        drawThread = DrawThread(gameSurfaceView.holder,nextFigureSurfaceView.holder,
            resources,scoresTextView,
            getSharedPreferences("data", Context.MODE_PRIVATE),recordTextView,infoBreathTextview,
            breathTextview,pauseButton, this.applicationContext)
        drawThread!!.setRunning(true)
        drawThread!!.start()

        leftButton.setOnTouchListener { view: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    drawThread!!.controller.Down = false
                    drawThread!!.controller.Up = false
                    drawThread!!.controller.Left = true
                    drawThread!!.controller.Right = false

                }
                MotionEvent.ACTION_MOVE -> {
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    drawThread!!.controller.Down = false
                    drawThread!!.controller.Up = false
                    drawThread!!.controller.Left = false
                    drawThread!!.controller.Right = false
                }
            }
            true
        }

        rightButton.setOnTouchListener { view: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    drawThread!!.controller.Down = false
                    drawThread!!.controller.Up = false
                    drawThread!!.controller.Left = false
                    drawThread!!.controller.Right = true

                }
                MotionEvent.ACTION_MOVE -> {
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    drawThread!!.controller.Down = false
                    drawThread!!.controller.Up = false
                    drawThread!!.controller.Left = false
                    drawThread!!.controller.Right = false
                }
            }
            true
        }

        downButton.setOnTouchListener { view: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    drawThread!!.controller.Down = true
                    drawThread!!.controller.Up = false
                    drawThread!!.controller.Left = false
                    drawThread!!.controller.Right = false

                }
                MotionEvent.ACTION_MOVE -> {
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    drawThread!!.controller.Down = false
                    drawThread!!.controller.Up = false
                    drawThread!!.controller.Left = false
                    drawThread!!.controller.Right = false
                }
            }
            true
        }

        rotateButton.setOnTouchListener { view: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    drawThread!!.controller.Down = false
                    drawThread!!.controller.Up = true
                    drawThread!!.controller.Left = false
                    drawThread!!.controller.Right = false

                }
                MotionEvent.ACTION_MOVE -> {
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    drawThread!!.controller.Down = false
                    drawThread!!.controller.Up = false
                    drawThread!!.controller.Left = false
                    drawThread!!.controller.Right = false
                }
            }
            true
        }

        newGameButton.setOnClickListener {
            drawThread!!.state.status = "new game"
        }
        pauseButton.setOnClickListener {
            drawThread!!.state.clickPause()
        }
        exitButton.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        var retry = true
        drawThread!!.setRunning(false)
        while (retry) {
            try {
                drawThread!!.join()
                retry = false
            } catch (e: InterruptedException) {
            }
        }
    }


}



