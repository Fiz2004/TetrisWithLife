package com.fiz.tetriswithlife.game.domain.grid

open class Point(var x: Int, var y: Int) {
    operator fun plus(p: Point): Point {
        return Point(this.x + p.x, this.y + p.y)
    }

    override operator fun equals(other: Any?): Boolean {
        if (other !is Point) return false

        if (this.x == other.x && this.y == other.y)
            return true

        return false
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}