package com.fiz.tetriswithlife.figure

import com.fiz.tetriswithlife.gameScreen.domain.repositories.RecordRepository
import com.fiz.tetriswithlife.gameScreen.game.Coordinate
import com.fiz.tetriswithlife.gameScreen.game.Game
import com.fiz.tetriswithlife.gameScreen.game.figure.CurrentFigure
import com.fiz.tetriswithlife.gameScreen.game.figure.Figure
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class CurrentFigureTest {
    private lateinit var game: Game
    private lateinit var currentFigure: CurrentFigure

    @Before
    fun setUp() {
        val recordRepository = mockk<RecordRepository>()
        game = Game(5, 10, recordRepository)
        val figure = Figure(getNumberFigure = 0)
        currentFigure =
            CurrentFigure.create(
                game.grid,
                figure,
                Coordinate(0.0, (0 - figure.getMaxY()).toDouble())
            )
    }

    @Test
    fun whenNotCollision_shouldReturnFalse() {
        val coordinate = Coordinate(0.0, 0.0)

        assertFalse(game.isCollision(coordinate))
    }

    @Test
    fun whenCoordinateOutsideGridForX_shouldReturnTrue() {
        val coordinate = Coordinate(12.0, 0.0)

        assertTrue(game.isCollision(coordinate))
    }

    @Test
    fun whenCoordinateOutsideGridForMinusY_shouldReturnFalse() {
        val coordinate = Coordinate(0.0, -2.0)

        assertFalse(game.isCollision(coordinate))
    }

    @Test
    fun whenCollision_shouldReturnTrue() {
        game.grid.space[9][3].block = 1
        val coordinate = Coordinate(0.0, 9.0)

        assertTrue(game.isCollision(coordinate))
    }
}