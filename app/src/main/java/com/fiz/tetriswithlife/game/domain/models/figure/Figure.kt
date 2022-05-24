package com.fiz.tetriswithlife.game.domain.models.figure

import com.fiz.tetriswithlife.game.domain.models.Vector
import java.io.Serializable

private const val NUMBER_IMAGES_FIGURE = 5

/*
    ****
*/
private val FIGURE1 = listOf(Vector(0, 1), Vector(1, 1), Vector(2, 1), Vector(3, 1))

/*
    **
     **
*/
private val FIGURE2 = listOf(Vector(1, 1), Vector(2, 1), Vector(2, 2), Vector(3, 2))

/*
    **
     *
     *
*/
private val FIGURE3 = listOf(Vector(1, 1), Vector(2, 1), Vector(2, 2), Vector(2, 3))

/*
    *
    **
     *
*/
private val FIGURE4 = listOf(Vector(1, 1), Vector(1, 2), Vector(2, 2), Vector(2, 3))

/*
    *
    **
    *
*/
private val FIGURE5 = listOf(Vector(1, 1), Vector(1, 2), Vector(2, 2), Vector(1, 3))

/*
    **
    **
*/
private val FIGURE6 = listOf(Vector(1, 1), Vector(1, 2), Vector(2, 1), Vector(2, 2))

/*
    **
    *
    *
*/
private val FIGURE7 = listOf(Vector(1, 1), Vector(2, 1), Vector(1, 2), Vector(1, 3))

val FIGURES: List<List<Vector>> = listOf(
    FIGURE1,
    FIGURE2,
    FIGURE3,
    FIGURE4,
    FIGURE5,
    FIGURE6,
    FIGURE7
)

data class Figure(
    private val getNumberFigure: Int = (FIGURES.indices).shuffled().first(),
    val cells: List<Cell> =
        FIGURES[getNumberFigure].map { Cell(it, (1..NUMBER_IMAGES_FIGURE).shuffled().first()) }
) : Serializable {

    fun getWidth(): Int {
        return cells.filter { it.vector.x != 0 }.size
    }

    fun getHeight(): Int {
        return cells.filter { it.vector.y != 0 }.size
    }

    fun getMaxX(): Int {
        return cells.maxByOrNull { it.vector.x }?.vector?.x ?: 0
    }

    fun getMaxY(): Int {
        return cells.maxByOrNull { it.vector.y }?.vector?.y ?: 0
    }

    fun getCellsRotate(): List<Cell> {
        return cells.map { it.getCellRotate() }
    }
}


