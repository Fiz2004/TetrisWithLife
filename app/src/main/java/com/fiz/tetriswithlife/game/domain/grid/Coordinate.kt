package com.fiz.tetriswithlife.game.domain.grid

class Coordinate(var x: Double, var y: Double) {
    operator fun plus(add: Coordinate): Coordinate {
        return Coordinate(this.x + add.x, this.y + add.y)
    }

    fun toPoint(): Point {
        return Point(x.toInt(), y.toInt())
    }
}