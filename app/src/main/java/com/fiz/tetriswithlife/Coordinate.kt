package com.fiz.tetriswithlife

class Coordinate(var x: Double, var y: Double) {
    fun plus(add: Coordinate): Coordinate {
        return Coordinate(x + add.x, y + add.y)
    }
}