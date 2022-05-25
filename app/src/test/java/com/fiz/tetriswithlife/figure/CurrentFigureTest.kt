package com.fiz.tetriswithlife.figure

import com.fiz.tetriswithlife.gameScreen.domain.models.Coordinate
import com.fiz.tetriswithlife.gameScreen.domain.models.Grid
import com.fiz.tetriswithlife.gameScreen.domain.models.figure.CurrentFigure
import com.fiz.tetriswithlife.gameScreen.domain.models.figure.Figure
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class CurrentFigureTest {
    private lateinit var grid: Grid
    private lateinit var figure: Figure
    private lateinit var currentFigure: CurrentFigure

    @Before
    fun setUp() {
        grid = Grid(5, 10, { 0 })
        figure = Figure(getNumberFigure = 0)
        currentFigure = CurrentFigure(figure, Coordinate(0.0, (0 - figure.getMaxY()).toDouble()))
    }

    @Test
    fun whenNotCollision_shouldReturnFalse() {
        val coordinate = Coordinate(0.0, 0.0)

        assertFalse(grid.isCollision(coordinate))
    }

    @Test
    fun whenCoordinateOutsideGridForX_shouldReturnTrue() {
        val coordinate = Coordinate(12.0, 0.0)

        assertTrue(grid.isCollision(coordinate))
    }

    @Test
    fun whenCoordinateOutsideGridForMinusY_shouldReturnFalse() {
        val coordinate = Coordinate(0.0, -2.0)

        assertFalse(grid.isCollision(coordinate))
    }

    @Test
    fun whenCollision_shouldReturnTrue() {
        grid.space[9][3].block = 1
        val coordinate = Coordinate(0.0, 9.0)

        assertTrue(grid.isCollision(coordinate))
    }
}