package com.fiz.tetriswithlife.gameScreen.game

import java.io.Serializable
import kotlin.random.Random

private const val NUMBER_IMAGES_BACKGROUND = 16
const val WIDTH_GRID: Int = 10
const val HEIGHT_GRID: Int = 20

class Grid(
    width: Int = WIDTH_GRID,
    height: Int = HEIGHT_GRID,
    valueFon: () -> Int = { Random.nextInt(NUMBER_IMAGES_BACKGROUND) }
) : Serializable {
    var space: List<List<Element>> = List(height)
    {
        List(width) {
            Element(valueFon())
        }
    }
        private set

    val width
        get() = space.first().size

    val countRowFull
        get() = space.fold(0) { acc, row ->
            acc + if (row.all { it.block != 0 })
                1
            else
                0
        }

    fun isCollisionPoint(point: Vector) = point.y >= space.size
            || (point.x !in space.first().indices)
            || isInside(point) && isNotFree(point)

    fun isInside(point: Vector) =
        point.y in space.indices && point.x in space[point.y].indices


    fun isOutside(point: Vector) =
        point.y !in space.indices || point.x !in space[point.y].indices


    fun isFree(point: Vector): Boolean = space[point.y][point.x].block == 0

    fun getFullCopySpace(): MutableList<MutableList<Int>> {
        return space.map { row -> row.map { it.block }.toMutableList() }.toMutableList()
    }

    fun removeRows() {
        for ((index, value) in space.withIndex())
            if (value.all { it.block != 0 })
                removeAndAddRow(index)
    }

    private fun removeAndAddRow(index: Int) {
        for (i in index downTo 1)
            for (j in space[i].indices)
                space[i][j].setElement(space[i - 1][j])
        space.first().forEach { it.setZero() }
    }

    fun isNotFree(point: Vector) = space[point.y][point.x].block != 0
}