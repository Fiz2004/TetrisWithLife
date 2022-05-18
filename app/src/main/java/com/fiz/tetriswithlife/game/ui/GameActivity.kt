package com.fiz.tetriswithlife.game.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.fiz.tetriswithlife.R
import com.fiz.tetriswithlife.databinding.ActivityGameBinding
import com.fiz.tetriswithlife.game.data.BitmapRepository
import com.fiz.tetriswithlife.game.domain.Display
import com.fiz.tetriswithlife.game.domain.GameState
import com.fiz.tetriswithlife.game.domain.character.TIMES_BREATH_LOSE
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.max

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

        gameViewModel.tryLoadState(savedInstanceState?.getSerializable(STATE) as? GameState)

        binding.gameSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {}

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                display = Display(
                    binding.gameSurfaceView.width,
                    binding.gameSurfaceView.height,
                    gameViewModel.gameState.value?.grid?.width ?: 0,
                    gameViewModel.gameState.value?.grid?.height ?: 0,
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

        gameViewModel.gameState.observe(this) {
            displayUpdate(it)
        }
    }

    private fun displayUpdate(gameState: GameState) {

        binding.gameSurfaceView.holder.lockCanvas(null)?.let {
            display.render(gameState, it)
            binding.gameSurfaceView.holder.unlockCanvasAndPost(it)
        }

        binding.nextFigureSurfaceView.holder.lockCanvas(null)?.let {
            display.renderInfo(gameState, it)
            binding.nextFigureSurfaceView.holder.unlockCanvasAndPost(it)
        }

        binding.scoresTextView.text = resources.getString(
            R.string.scores_game_textview, gameState.scores.toString().padStart(6, '0')
        )
        binding.recordTextview.text =
            resources.getString(
                R.string.record_game_textview,
                gameState.record.toString().padStart(6, '0')
            )

        val textOnButtonPause = if (gameState.status == "pause")
            R.string.resume_game_button
        else
            R.string.pause_game_button

        binding.pauseButton.text = resources.getString(textOnButtonPause)

        binding.infoBreathTextView.visibility = if (gameState.character.breath)
            View.INVISIBLE
        else
            View.VISIBLE

        val sec: Double = if (gameState.character.breath)
            TIMES_BREATH_LOSE
        else
            max(gameState.character.timeBreath, 0.0)
        val cl = ((floor(sec) * 255) / TIMES_BREATH_LOSE).toInt()

        binding.breathTextView.visibility = (if (gameState.character.breath)
            View.INVISIBLE
        else
            View.VISIBLE)

        binding.breathTextView.text = sec.toInt().toString()

        binding.breathTextView.setTextColor(Color.rgb(255, cl, cl))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bindListener() {
        this.binding.leftButton.setOnTouchListener { _: View, event: MotionEvent ->
            gameViewModel.clickLeftButton(event)
            true
        }

        binding.rightButton.setOnTouchListener { _: View, event: MotionEvent ->
            gameViewModel.clickRightButton(event)
            true
        }

        binding.downButton.setOnTouchListener { _: View, event: MotionEvent ->
            gameViewModel.clickDownButton(event)
            true
        }

        binding.rotateButton.setOnTouchListener { _: View, event: MotionEvent ->
            gameViewModel.clickRotateButton(event)
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

    // Сериализация не проходит
    override fun onSaveInstanceState(outState: Bundle) {
        gameViewModel.gameState.value?.let {
//            outState.putSerializable(STATE, it)
        }
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val STATE = "state"
    }

}



