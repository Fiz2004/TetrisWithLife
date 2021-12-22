package com.fiz.tetriswithlife

open class Point(var x:Float, var y:Float) {
    constructor(x:Int, y:Int) : this(x.toFloat(),y.toFloat())
    constructor(x:Double, y:Double) : this(x.toFloat(),y.toFloat())

    fun plus(p:Point):Point{
        return Point(x+p.x,y+p.y)
    }
}