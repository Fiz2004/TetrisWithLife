package com.fiz.tetriswithlife


private const val NUMBER_IMAGES_BACKGROUND = 16

class Grid(val width: Int, val height: Int) {
    val space: Array<Array<Element>> = Array(height) {
        Array(width) {
            Element((0 until NUMBER_IMAGES_BACKGROUND).shuffled().first())
        }
    }

    fun isInside(p: Point): Boolean {
        return p.x in 0 until width && p.y in 0 until height
    }

    fun isOutside(p: Point): Boolean {
        return p.x !in 0 until width || p.y !in 0 until height
    }

//    fun isCanMove(p: Point): Boolean {
//        return isOutside(p) || isNotFree(p)
//    }

    fun isFree(p: Point): Boolean {
        return this.space[p.y][p.x].block == 0
    }

    fun isNotFree(p: Point): Boolean {
        return this.space[p.y][p.x].block != 0
    }

    fun getCountRowFull(): Int {
        var result = 0
        for (row in space) {
            if (row.all { element ->
                    element.block != 0
                })
                result += 1
        }
        return result
    }

    fun deleteRows() {
        for ((index,value) in space.withIndex()) {
            if (value.all { element ->
                    element.block != 0
                }) {
                deleteRow(index)
                space[0].forEach { element -> element.setZero() }
            }
        }
    }

    private fun deleteRow(rowIndex: Int) {
        for (i in rowIndex downTo 1)
            for (j in 0 until width)
                space[i][j].setElement(this.space[i - 1][j])
    }
}
