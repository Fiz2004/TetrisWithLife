package com.fiz.tetriswithlife.figure

import com.fiz.tetriswithlife.gameScreen.domain.repositories.RecordRepository
import com.fiz.tetriswithlife.gameScreen.game.Coordinate
import com.fiz.tetriswithlife.gameScreen.game.Game
import com.fiz.tetriswithlife.gameScreen.game.Vector
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
                game.grid.space.first().size,
                figure,
                Coordinate(0.0, (0 - figure.getMaxY()).toDouble())
            )
    }

    @Test
    fun whenNotCollision_shouldReturnFalse() {
        val coordinate = Vector(0, 0)

        assertFalse(game.grid.isCollisionPoint(coordinate))
    }

    @Test
    fun whenCoordinateOutsideGridForX_shouldReturnTrue() {
        val coordinate = Vector(12, 0)

        assertTrue(game.grid.isCollisionPoint(coordinate))
    }

    @Test
    fun whenCoordinateOutsideGridForMinusY_shouldReturnFalse() {
        val coordinate = Vector(0, -2)

        assertFalse(game.grid.isCollisionPoint(coordinate))
    }

    @Test
    fun whenCollision_shouldReturnTrue() {
        game.grid.space[9][3].block = 1
        val coordinate = Vector(0, 9)

        assertTrue(game.grid.isCollisionPoint(coordinate))
    }
}