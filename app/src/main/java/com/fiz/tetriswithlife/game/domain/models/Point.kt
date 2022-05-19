package com.fiz.tetriswithlife.game.domain.models

import java.io.Serializable

data class Point(val x: Int, val y: Int) : Serializable {
    operator fun plus(p: Point): Point {
        return Point(this.x + p.x, this.y + p.y)
    }

    operator fun times(tile: Int): Point {
        return Point(this.x * tile, this.y * tile)
    }
}