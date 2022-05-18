package com.fiz.tetriswithlife.game.domain.models

private const val NUMBER_IMAGES_FIGURE = 5

/*
    ****
*/
private val FIGURE1 = listOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(3, 1))

/*
    **
     **
*/
private val FIGURE2 = listOf(Point(1, 1), Point(2, 1), Point(2, 2), Point(3, 2))

/*
    **
     *
     *
*/
private val FIGURE3 = listOf(Point(1, 1), Point(2, 1), Point(2, 2), Point(2, 3))

/*
    *
    **
     *
*/
private val FIGURE4 = listOf(Point(1, 1), Point(1, 2), Point(2, 2), Point(2, 3))

/*
    *
    **
    *
*/
private val FIGURE5 = listOf(Point(1, 1), Point(1, 2), Point(2, 2), Point(1, 3))

/*
    **
    **
*/
private val FIGURE6 = listOf(Point(1, 1), Point(1, 2), Point(2, 1), Point(2, 2))

/*
    **
    *
    *
*/
private val FIGURE7 = listOf(Point(1, 1), Point(2, 1), Point(1, 2), Point(1, 3))

val FIGURES: List<List<Point>> = listOf(
    FIGURE1,
    FIGURE2,
    FIGURE3,
    FIGURE4,
    FIGURE5,
    FIGURE6,
    FIGURE7
)

data class Figure(
    private val getNumberFigure: () -> Int = { (FIGURES.indices).shuffled().first() },
    val cells: List<Cell> =
        FIGURES[getNumberFigure()].map { Cell(it, (1..NUMBER_IMAGES_FIGURE).shuffled().first()) }
) {

    fun getMaxX(): Int {
        return cells.maxByOrNull { it.point.x }?.point?.x ?: 0
    }

    fun getMaxY(): Int {
        return cells.maxByOrNull { it.point.y }?.point?.y ?: 0
    }
}


