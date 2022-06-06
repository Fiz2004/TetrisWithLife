package com.fiz.tetriswithlife.gameScreen.game

import java.io.Serializable
import kotlin.math.roundToInt

data class Coordinate(val x: Double, val y: Double) : Serializable {

    val posTile
        get() = Vector(posTileX, posTileY)

    val toPoint
        get() = Vector(x.toInt(), y.toInt())

    private val posTileX: Int
        get() = x.roundToInt()

    private val posTileY: Int
        get() = y.roundToInt()

    operator fun plus(add: Coordinate): Coordinate {
        return Coordinate(this.x + add.x, this.y + add.y)
    }

    operator fun times(tile: Int): Coordinate {
        return Coordinate(this.x * tile, this.y * tile)
    }

    operator fun times(tile: Double): Coordinate {
        return Coordinate(this.x * tile, this.y * tile)
    }
}