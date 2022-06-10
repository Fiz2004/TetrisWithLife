package com.fiz.tetriswithlife.figure

import com.fiz.tetriswithlife.gameScreen.game.Coordinate
import com.fiz.tetriswithlife.gameScreen.game.Grid
import com.fiz.tetriswithlife.gameScreen.game.Vector
import com.fiz.tetriswithlife.gameScreen.game.figure.CurrentFigure
import com.fiz.tetriswithlife.gameScreen.game.figure.Figure
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class CurrentFigureTest {
    private lateinit var grid: Grid
    private lateinit var currentFigure: CurrentFigure

    @Before
    fun setUp() {
        grid = Grid(5, 10)
        val figure = Figure(getNumberFigure = 0)
        currentFigure =
            CurrentFigure.create(
                grid.width,
                figure,
                Coordinate(0.0, (0 - figure.getMaxY()).toDouble())
            )
    }

    @Test
    fun whenNotCollision_shouldReturnFalse() {
        val coordinate = Vector(0, 0)

        assertFalse(grid.isCollisionPoint(coordinate))
    }

    @Test
    fun whenCoordinateOutsideGridForX_shouldReturnTrue() {
        val coordinate = Vector(12, 0)

        assertTrue(grid.isCollisionPoint(coordinate))
    }

    @Test
    fun whenCoordinateOutsideGridForMinusY_shouldReturnFalse() {
        val coordinate = Vector(0, -2)

        assertFalse(grid.isCollisionPoint(coordinate))
    }

    @Test
    fun whenCollision_shouldReturnTrue() {
        grid.space[9][3].setBlock(1)
        val coordinate = Vector(0, 9)

        assertTrue(grid.isCollisionPoint(coordinate))
    }
}