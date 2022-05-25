package com.fiz.tetriswithlife.gameScreen.game

import java.io.Serializable
import kotlin.random.Random

private const val NUMBER_IMAGES_BACKGROUND = 16

class Grid private constructor(val space: List<List<Element>>) : Serializable {

    val countRowFull
        get() = space.fold(0) { acc, row ->
            acc + if (row.all { it.block != 0 })
                1
            else
                0
        }

    fun isInside(point: Vector) = point.y in space.indices && point.x in space[point.y].indices

    fun isOutside(point: Vector) = point.y !in space.indices || point.x !in space[point.y].indices

    fun isFree(point: Vector): Boolean = space[point.y][point.x].block == 0

    fun isNotFree(point: Vector) = space[point.y][point.x].block != 0

    fun isCollision(point: Vector) = point.y >= space.size
            || (point.x !in space.first().indices)
            || isInside(point) && isNotFree(point)

    fun deleteRows() {
        for ((index, value) in space.withIndex())
            if (value.all { it.block != 0 }) {
                for (i in index downTo 1)
                    for (j in space[i].indices)
                        space[i][j].setElement(space[i - 1][j])
                space.first().forEach { it.setZero() }
            }
    }

    companion object {
        fun create(
            width: Int,
            height: Int,
            valueFon: () -> Int = { Random.nextInt(NUMBER_IMAGES_BACKGROUND) }
        ): Grid {
            return Grid(
                space = List(height) {
                    List(width) {
                        Element(valueFon())
                    }
                }
            )
        }
    }
}