package com.fiz.tetriswithlife.grid

open class Point(var x:Int, var y:Int) {
    operator fun plus(p: Point): Point {
        return Point(this.x+p.x,this.y+p.y)
    }

     override operator fun equals(p:Any?):Boolean{
        if (p is Point) {
            if (this.x == p.x && this.y == p.y)
                return true
        }
         return false
    }
}