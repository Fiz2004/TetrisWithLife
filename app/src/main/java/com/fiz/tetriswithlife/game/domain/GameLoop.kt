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

        surface.holder.lockCanvas(null)?.let{
            display.render(state, it)
            surface.holder.unlockCanvasAndPost(it)
        }

        surfaceNextFigure.holder.lockCanvas(null)?.let{
            display.renderInfo(state, it)
            surfaceNextFigure.holder.unlockCanvasAndPost(it)
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