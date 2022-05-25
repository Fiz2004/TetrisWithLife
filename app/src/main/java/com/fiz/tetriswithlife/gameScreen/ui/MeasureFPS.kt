package com.fiz.tetriswithlife.gameScreen.ui

import android.util.Log

class MeasureFPS {
    var lastTime = System.currentTimeMillis()
    var fps = 60

    operator fun invoke(body: () -> Unit) {
        val now = System.currentTimeMillis()
        val deltaTime = now - lastTime
        fps = ((fps + (1000 / deltaTime)) / 2).toInt()

        Log.d("FPS", fps.toString())

        body()

        lastTime = now
    }
}