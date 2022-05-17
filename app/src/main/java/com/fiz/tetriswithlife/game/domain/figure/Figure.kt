package com.fiz.tetriswithlife.game.domain.figure

private const val NUMBER_IMAGES_FIGURE = 5

val FIGURE: List<List<Point>> = listOf(
    listOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(3, 1)),
    listOf(Point(1, 1), Point(2, 1), Point(2, 2), Point(3, 2)),
    listOf(Point(1, 1), Point(2, 1), Point(2, 2), Point(2, 3)),
    listOf(Point(1, 1), Point(1, 2), Point(2, 2), Point(2, 3)),
    listOf(Point(1, 1), Point(1, 2), Point(2, 2), Point(1, 3)),
    listOf(Point(1, 1), Point(1, 2), Point(2, 1), Point(2, 2)),
    listOf(Point(1, 1), Point(2, 1), Point(1, 2), Point(1, 3))
)

data class Figure(
    private val getNumberFigure: () -> Int = { (FIGURE.indices).shuffled().first() },
    val cells: List<Cell> =
        FIGURE[getNumberFigure()].map { Cell(it, (1..NUMBER_IMAGES_FIGURE).shuffled().first()) }
) {

    fun getWidth(): Int {
        return cells.maxByOrNull { it.point.x }?.point?.x ?: 0
    }

    fun getHeight(): Int {
        return cells.maxByOrNull { it.point.y }?.point?.y ?: 0
    }
}


