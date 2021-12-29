package com.fiz.tetriswithlife.grid

class Coordinate(var x:Double, var y:Double) {
    operator fun plus(add: Coordinate): Coordinate {
        return Coordinate(this.x+add.x,this.y+add.y)
    }
}