package com.fiz.tetriswithlife

import android.content.res.Resources
import android.graphics.*
import android.util.Log
import android.view.SurfaceHolder

private const val TIME_UPDATE_CONTROLLER = 80

data class Controller(
    var Down: Boolean = false,
    var Up: Boolean = false,
    var Left: Boolean = false,
    var Right: Boolean = false,
)

class DrawThread(private val surfaceHolder: SurfaceHolder, resources: Resources) : Thread() {
    private val widthCanvas: Int = 13
    private val heightCanvas: Int = 25
    private var prevTime = System.currentTimeMillis()
    private var state = State(widthCanvas, heightCanvas)
    private val display = Display(resources, widthCanvas, heightCanvas)

    val controller = Controller()

    private var running = false

    fun setRunning(running: Boolean) {
        this.running = running
    }

    override fun run() {
        var canvas: Canvas?
        while (running) {
            val now = System.currentTimeMillis()
            val deltaTime = now - prevTime
            if (deltaTime > TIME_UPDATE_CONTROLLER) {
                prevTime = now
            }
            canvas = null
            try {
                canvas = surfaceHolder.lockCanvas(null)
                if (canvas == null) continue
                synchronized(surfaceHolder) {
                    var status=true
                    if (deltaTime > TIME_UPDATE_CONTROLLER) {
                        status=state.update(deltaTime.toFloat(), controller)
                    }
                    display.render(state, canvas)
                    if (!status)
                        state = State(widthCanvas, heightCanvas)
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }
}