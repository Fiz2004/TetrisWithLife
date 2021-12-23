package com.fiz.tetriswithlife


import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.view.View.OnTouchListener





private const val TIMES_BREATH_LOSE = 60
private const val DIRECTORY_IMG = "Resurs/v1/"

class GameActivity : AppCompatActivity() {
    private lateinit var scoresTextView: TextView
    private lateinit var recordTextView: TextView
    private lateinit var surfaceView: SurfaceView
    private lateinit var newGameButton: Button
    private lateinit var pauseButton: Button
    private lateinit var exitButton: Button

    private lateinit var leftButton: Button
    private lateinit var downButton: Button
    private lateinit var rightButton: Button
    private lateinit var rotateButton: Button

    private var drawThread: DrawThread? = null

    var x = 0
    var y = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        scoresTextView = findViewById(R.id.scores_game_textView)
        recordTextView = findViewById(R.id.record_game_textView)
        surfaceView = findViewById(R.id.gamesurface_game_surfaceView)
        newGameButton = findViewById(R.id.new_game_game_button)
        pauseButton = findViewById(R.id.pause_game_button)
        exitButton = findViewById(R.id.exit_game_button)

        leftButton = findViewById(R.id.left_game_button)
        downButton = findViewById(R.id.down_game_button)
        rightButton = findViewById(R.id.right_game_button)
        rotateButton = findViewById(R.id.rotate_game_button)

        drawThread = DrawThread(surfaceView.getHolder(), getResources())
        drawThread!!.setRunning(true)
        drawThread!!.start()

        leftButton.setOnClickListener {
            drawThread!!.controller.Down = false
            drawThread!!.controller.Up = false
            drawThread!!.controller.Left = true
            drawThread!!.controller.Right = false
        }

        leftButton.setOnTouchListener(OnTouchListener { v, event ->
            when (event!!.action) {
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
        })

        rightButton.setOnTouchListener(OnTouchListener { v, event ->
            when (event!!.action) {
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
        })

        downButton.setOnTouchListener(OnTouchListener { v, event ->
            when (event!!.action) {
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
        })

        rotateButton.setOnTouchListener(OnTouchListener { v, event ->
            when (event!!.action) {
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
        })


//        surfaceView.setOnClickListener(this)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        return super.onTouchEvent(event)

        x = event!!.x.toInt()
        y = event!!.y.toInt()


        val displaymetrics = resources.displayMetrics

        Log.d("Touch event!!.action=", event!!.action.toString())

        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                if (x < displaymetrics.widthPixels / 3) {
                    drawThread!!.controller.Down = false
                    drawThread!!.controller.Up = false
                    drawThread!!.controller.Left = true
                    drawThread!!.controller.Right = false
                }
                if (x > 2 * displaymetrics.widthPixels / 3) {
                    drawThread!!.controller.Down = false
                    drawThread!!.controller.Up = false
                    drawThread!!.controller.Left = false
                    drawThread!!.controller.Right = true
                }
                if (y < displaymetrics.heightPixels / 6) {
                    drawThread!!.controller.Down = false
                    drawThread!!.controller.Up = true
                    drawThread!!.controller.Left = false
                    drawThread!!.controller.Right = false
                }
                if (y > 5 * displaymetrics.heightPixels / 6) {
                    drawThread!!.controller.Down = true
                    drawThread!!.controller.Up = false
                    drawThread!!.controller.Left = false
                    drawThread!!.controller.Right = false
                }
            }
            MotionEvent.ACTION_MOVE -> {
//                Log.d("Touch Move x=",x.toString())
//                Log.d("Touch Move y=",y.toString())
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                drawThread!!.controller.Down = false
                drawThread!!.controller.Up = false
                drawThread!!.controller.Left = false
                drawThread!!.controller.Right = false
            }
        }
//        tv.setText(sDown.toString() + "\n" + sMove + "\n" + sUp)
        return true
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

