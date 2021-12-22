package com.fiz.tetriswithlife

private const val NUMBER_IMAGES_FIGURE = 4

val FIGURE: Array<Array<Array<Int>>> = arrayOf(
    arrayOf(arrayOf(0, 1), arrayOf(1, 1), arrayOf(2, 1), arrayOf(3, 1)),
    arrayOf(arrayOf(1, 1), arrayOf(2, 1), arrayOf(2, 2), arrayOf(3, 2)),
    arrayOf(arrayOf(1, 1), arrayOf(2, 1), arrayOf(2, 2), arrayOf(2, 3)),
    arrayOf(arrayOf(1, 1), arrayOf(1, 2), arrayOf(2, 2), arrayOf(2, 3)),
    arrayOf(arrayOf(1, 1), arrayOf(1, 2), arrayOf(2, 2), arrayOf(1, 3)),
    arrayOf(arrayOf(1, 1), arrayOf(1, 2), arrayOf(2, 1), arrayOf(2, 2)),
    arrayOf(arrayOf(1, 1), arrayOf(2, 1), arrayOf(1, 2), arrayOf(1, 3))
)

const val NUMBER_CELL = 4

open class Figure {
    var cells=emptyArray<Cell>()

    companion object {
        fun numberCell(): Int = NUMBER_CELL
    }

    init {
        createFigure()
    }

    fun createFigure() {
        cells=emptyArray()
        for (p in FIGURE[(0 until FIGURE.size).shuffled().first()]) {
            val view = (1..NUMBER_IMAGES_FIGURE).shuffled().first()
            cells += Cell(p[0].toFloat(), p[1].toFloat(), view)
        }
    }
}


