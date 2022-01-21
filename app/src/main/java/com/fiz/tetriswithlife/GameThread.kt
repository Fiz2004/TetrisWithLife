package com.fiz.tetriswithlife

import android.content.Context
import android.graphics.Canvas
import android.view.SurfaceHolder
import android.widget.Button
import android.widget.TextView
import kotlin.math.min

private const val widthCanvas: Int = 13
private const val heightCanvas: Int = 25

class GameThread(
    private val surfaceHolder: SurfaceHolder,
    private val nextFigureSurfaceHolder: SurfaceHolder,
    private val context: Context,
    scoresTextView: TextView,
    recordTextView: TextView,
    infoBreathTextview: TextView,
    breathTextview: TextView,
    pauseButton: Button
) : Thread() {
    var state = State(
        widthCanvas, heightCanvas, context.getSharedPreferences("data", Context.MODE_PRIVATE)
    )
    val controller = Controller()

    private var prevTime = System.currentTimeMillis()
    private var ending = 1.0
    private var running = false

    private val display = Display(
        context.resources, scoresTextView,
        recordTextView, infoBreathTextview, breathTextview, pauseButton,
        context
    )


    fun setRunning(running: Boolean) {
        this.running = running
    }

    override fun run() {
        var canvas: Canvas?
        var nextFigureCanvas: Canvas?
        while (running) {
            stateUpdate()

            canvas = null
            nextFigureCanvas = null
            try {
                canvas = surfaceHolder.lockCanvas(null)
                if (canvas == null) continue
                synchronized(surfaceHolder) {
                    display.render(state, canvas)
                }

                nextFigureCanvas = nextFigureSurfaceHolder.lockCanvas(null)
                if (nextFigureCanvas == null) continue
                synchronized(nextFigureSurfaceHolder) {
                    display.renderInfo(state, nextFigureCanvas)
                }

            } finally {
                if (canvas != null)
                    surfaceHolder.unlockCanvasAndPost(canvas)
                if (nextFigureCanvas != null)
                    nextFigureSurfaceHolder.unlockCanvasAndPost(nextFigureCanvas)

            }
        }
    }

    private fun stateUpdate() {
        val now = System.currentTimeMillis()
        val deltaTime = min(now - prevTime, 100).toInt()/1000.0

        if (state.status != "pause") {
            var status = true
            if (ending == 1.0)
                status = state.update(controller, deltaTime)

            if (!status || ending != 1.0)
                ending -= deltaTime
        }

        if (ending < 0 || state.status == "new game") {
            state = State(
                widthCanvas,
                heightCanvas,
                context.getSharedPreferences("data", Context.MODE_PRIVATE)
            )
            ending = 1.0
        }

        prevTime = now
    }
}