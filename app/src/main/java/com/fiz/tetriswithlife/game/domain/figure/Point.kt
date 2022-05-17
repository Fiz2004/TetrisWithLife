package com.fiz.tetriswithlife.game.domain.figure

data class Point(var x: Int, var y: Int) {
    operator fun plus(p: Point): Point {
        return Point(this.x + p.x, this.y + p.y)
    }
}