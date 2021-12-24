package com.fiz.tetriswithlife

open class Point(var x:Int, var y:Int) {
    fun plus(p:Point):Point{
        return Point(x+p.x,y+p.y)
    }
}