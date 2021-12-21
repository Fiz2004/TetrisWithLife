package com.fiz.tetriswithlife

open class Point(var x:Double, var y:Double) {
    fun plus(p:Point):Point{
        return Point(x+p.x,y+p.y)
    }
}