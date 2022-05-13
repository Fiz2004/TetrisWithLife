package com.fiz.tetriswithlife.game.domain

import android.graphics.Canvas
import android.view.SurfaceView
import kotlin.math.min

class GameLoop(
    var state: State,
    private var display: Display,
    val controller: Controller,
    private val surface: SurfaceView,
    private val surfaceNextFigure: SurfaceView
) {
    private var prevTime = System.currentTimeMillis()
    private var ending = 1.0
    private var running = false

    fun setRunning(running: Boolean) {
        this.running = running
    }

    fun run() {

        while (running) {

            stateUpdate()

            displayUpdate()
        }

    }

    private fun displayUpdate() {
        var canvas: Canvas? = null
        var nextFigureCanvas: Canvas? = null

        try {
            canvas = surface.holder.lockCanvas(null)
            if (canvas == null) return
            synchronized(surface.holder) {
                display.render(state, canvas)
            }

            nextFigureCanvas = surfaceNextFigure.holder.lockCanvas(null)
            if (nextFigureCanvas == null) return
            synchronized(surfaceNextFigure.holder) {
                display.renderInfo(state, nextFigureCanvas)
            }

        } finally {
            if (canvas != null)
                surface.holder.unlockCanvasAndPost(canvas)
            if (nextFigureCanvas != null)
                surfaceNextFigure.holder.unlockCanvasAndPost(nextFigureCanvas)

        }
    }

    private fun stateUpdate() {
        val now = System.currentTimeMillis()
        val deltaTime = min(now - prevTime, 100).toInt() / 1000.0

        if (state.status != "pause") {
            var status = true
            if (ending == 1.0)
                status = state.update(controller, deltaTime)

            if (!status || ending != 1.0)
                ending -= deltaTime
        }

        if (ending < 0 || state.status == "new game") {
            state.new()
            ending = 1.0
        }

        prevTime = now
    }
}