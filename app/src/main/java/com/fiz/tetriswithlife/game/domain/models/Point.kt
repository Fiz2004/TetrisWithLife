package com.fiz.tetriswithlife.game.domain.models

data class Point(val x: Int, val y: Int) {
    operator fun plus(p: Point): Point {
        return Point(this.x + p.x, this.y + p.y)
    }

    operator fun times(tile: Int): Point {
        return Point(this.x * tile, this.y * tile)
    }
}