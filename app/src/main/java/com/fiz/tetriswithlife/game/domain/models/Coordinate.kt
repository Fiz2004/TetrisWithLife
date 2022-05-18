package com.fiz.tetriswithlife.game.domain.models

data class Coordinate(val x: Double, val y: Double) {
    operator fun plus(add: Coordinate): Coordinate {
        return Coordinate(this.x + add.x, this.y + add.y)
    }

    fun toPoint(): Point {
        return Point(x.toInt(), y.toInt())
    }
}