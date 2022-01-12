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

        gameThread = GameThread(gameSurfaceView.holder,nextFigureSurfaceView.holder,
            resources,scoresTextView,
            getSharedPreferences("data", Context.MODE_PRIVATE),recordTextView,infoBreathTextview,
            breathTextview,pauseButton, this.applicationContext)
        gameThread!!.setRunning(true)
        gameThread!!.start()

        leftButton.setOnTouchListener { view: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    gameThread!!.controller.Down = false
                    gameThread!!.controller.Up = false
                    gameThread!!.controller.Left = true
                    gameThread!!.controller.Right = false
                }
                MotionEvent.ACTION_MOVE -> {
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    gameThread!!.controller.Down = false
                    gameThread!!.controller.Up = false
                    gameThread!!.controller.Left = false
                    gameThread!!.controller.Right = false
                }
            }
            true
        }

        rightButton.setOnTouchListener { view: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    gameThread!!.controller.Down = false
                    gameThread!!.controller.Up = false
                    gameThread!!.controller.Left = false
                    gameThread!!.controller.Right = true
                }
                MotionEvent.ACTION_MOVE -> {
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    gameThread!!.controller.Down = false
                    gameThread!!.controller.Up = false
                    gameThread!!.controller.Left = false
                    gameThread!!.controller.Right = false
                }
            }
            true
        }

        downButton.setOnTouchListener { view: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    gameThread!!.controller.Down = true
                    gameThread!!.controller.Up = false
                    gameThread!!.controller.Left = false
                    gameThread!!.controller.Right = false
                }
                MotionEvent.ACTION_MOVE -> {
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    gameThread!!.controller.Down = false
                    gameThread!!.controller.Up = false
                    gameThread!!.controller.Left = false
                    gameThread!!.controller.Right = false
                }
            }
            true
        }

        rotateButton.setOnTouchListener { view: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    gameThread!!.controller.Down = false
                    gameThread!!.controller.Up = true
                    gameThread!!.controller.Left = false
                    gameThread!!.controller.Right = false
                }
                MotionEvent.ACTION_MOVE -> {
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    gameThread!!.controller.Down = false
                    gameThread!!.controller.Up = false
                    gameThread!!.controller.Left = false
                    gameThread!!.controller.Right = false
                }
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
            }
        }
    }


}



