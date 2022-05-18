package com.fiz.tetriswithlife.figure

import com.fiz.tetriswithlife.game.domain.figure.CurrentFigure
import com.fiz.tetriswithlife.game.domain.grid.Grid
import com.fiz.tetriswithlife.game.domain.models.Coordinate
import com.fiz.tetriswithlife.game.domain.models.Figure
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
    figure = Figure(getNumberFigure = { 0 })
    currentFigure = CurrentFigure(grid, figure, getStartX = { 0 })
  }

  @Test
  fun whenNotCollision_shouldReturnFalse() {
    val coordinate = Coordinate(0.0, 0.0)

    assertFalse(currentFigure.isCollission(coordinate))
  }

  @Test
  fun whenCoordinateOutsideGridForX_shouldReturnTrue() {
    val coordinate = Coordinate(12.0, 0.0)

    assertTrue(currentFigure.isCollission(coordinate))
  }

  @Test
  fun whenCoordinateOutsideGridForMinusY_shouldReturnFalse() {
    val coordinate = Coordinate(0.0, -2.0)

    assertFalse(currentFigure.isCollission(coordinate))
  }

  @Test
  fun whenCollision_shouldReturnTrue() {
    grid.space[9][3].block = 1
    val coordinate = Coordinate(0.0, 9.0)

    assertTrue(currentFigure.isCollission(coordinate))
  }
}