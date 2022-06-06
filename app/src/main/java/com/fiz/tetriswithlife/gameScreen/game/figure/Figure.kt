package com.fiz.tetriswithlife.gameScreen.game.figure

import com.fiz.tetriswithlife.gameScreen.game.Vector
import java.io.Serializable
import kotlin.random.Random
import kotlin.random.nextInt

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
    private val getNumberFigure: Int = Random.nextInt(FIGURES.indices),
    val cells: List<Cell> =
        FIGURES[getNumberFigure].map { Cell(it, Random.nextInt(1..NUMBER_IMAGES_FIGURE)) }
) : Serializable {

    fun getWidth(): Int {
        return cells.map { it.vector.x }.distinct().size
    }

    fun getHeight(): Int {
        return cells.map { it.vector.y }.distinct().size
    }

    fun getMaxX(): Int {
        return cells.maxOf { it.vector.x }
    }

    fun getMaxY(): Int {
        return cells.maxOf { it.vector.y }
    }

    fun getCellsRotate(): List<Cell> {
        return cells.map { it.cellRotate }
    }
}


