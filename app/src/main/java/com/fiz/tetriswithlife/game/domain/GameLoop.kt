package com.fiz.tetriswithlife.game.domain

import android.view.SurfaceView
import com.fiz.tetriswithlife.game.data.RecordRepository
import kotlin.math.min

class GameLoop(
    var gameState: GameState,
    private var display: Display,
    val controller: Controller,
    private val surface: SurfaceView,
    private val surfaceNextFigure: SurfaceView,
    private val recordRepository: RecordRepository
) {
    private var prevTime = System.currentTimeMillis()
    private var ending = 1.0
    var running = false

    fun displayUpdate() {

        surface.holder.lockCanvas(null)?.let {
            display.render(gameState, it)
            surface.holder.unlockCanvasAndPost(it)
        }

        surfaceNextFigure.holder.lockCanvas(null)?.let {
            display.renderInfo(gameState, it)
            surfaceNextFigure.holder.unlockCanvasAndPost(it)
        }

    }

    fun stateUpdate() {
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
}