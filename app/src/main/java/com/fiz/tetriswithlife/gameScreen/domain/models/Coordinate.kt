package com.fiz.tetriswithlife.gameScreen.domain.models

import java.io.Serializable
import kotlin.math.roundToInt

data class Coordinate(val x: Double, val y: Double): Serializable {
    operator fun plus(add: Coordinate): Coordinate {
        return Coordinate(this.x + add.x, this.y + add.y)
    }

    fun toPoint(): Vector {
        return Vector(x.toInt(), y.toInt())
    }

    val posTileX: Int
        get() = x.roundToInt()

    val posTileY: Int
        get() = y.roundToInt()

    val posTile
        get() = Vector(posTileX, posTileY)
}