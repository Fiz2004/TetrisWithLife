package com.fiz.tetriswithlife.figure

import com.fiz.tetriswithlife.grid.Coordinate
import com.fiz.tetriswithlife.grid.Grid
import com.fiz.tetriswithlife.grid.Point
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("CurrentFigureTest")
internal class CurrentFigureTest {
  private lateinit var grid:Grid
  private lateinit var figure:Figure
  private lateinit var currentFigure:CurrentFigure

  @BeforeEach
  fun setUp() {
    grid= Grid(5,10) { 0 }
    figure=Figure{0}
    var currentFigure=CurrentFigure(grid,figure) { 0 }
  }

  @Test
  @DisplayName("isCollission")
  fun isCollission() {
    var coordinate= Coordinate(0.0,0.0)
    assertFalse(currentFigure.isCollission(coordinate))

    coordinate= Coordinate(12.0,0.0)
    assertTrue(currentFigure.isCollission(coordinate))

    coordinate= Coordinate(0.0,-2.0)
    assertFalse(currentFigure.isCollission(coordinate))

    grid.space[9][3].block=1
    coordinate= Coordinate(0.0,9.0)
    assertTrue(currentFigure.isCollission(coordinate))
  }
}