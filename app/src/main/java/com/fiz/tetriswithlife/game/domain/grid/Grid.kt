package com.fiz.tetriswithlife.game.domain.grid

import com.fiz.tetriswithlife.game.domain.models.Point

private const val NUMBER_IMAGES_BACKGROUND = 16

data class Grid(
    val width: Int, val height: Int,
    val valueFon: () -> Int = { (0 until NUMBER_IMAGES_BACKGROUND).shuffled().first() },
    val space: List<List<Element>> = List(height) {
        List(width) {
            Element(valueFon())
        }
    }
) {

    fun isInside(p: Point): Boolean {
        return p.x in 0 until width && p.y in 0 until height
    }

    fun isOutside(p: Point): Boolean {
        return p.x !in 0 until width || p.y !in 0 until height
    }

    fun isFree(p: Point): Boolean {
        return this.space[p.y][p.x].block == 0
    }

    fun isNotFree(p: Point): Boolean {
        return this.space[p.y][p.x].block != 0
    }

    fun getCountRowFull(): Int {
        var result = 0
        for (row in space)
            if (row.all { it.block != 0 })
                result += 1

        return result
    }

    fun deleteRows(): Grid {
        val newSpace = space.toMutableList()

        for ((index, value) in newSpace.withIndex())
            if (value.all { it.block != 0 }) {
                for (i in index downTo 1)
                    for (j in 0 until width)
                        newSpace[i][j].setElement(newSpace[i - 1][j])
                newSpace[0].forEach { it.setZero() }
            }

        return copy(space = newSpace)
    }

}
