package com.fiz.tetriswithlife

import com.fiz.tetriswithlife.gameScreen.domain.repositories.RecordRepository
import com.fiz.tetriswithlife.gameScreen.game.Game
import com.fiz.tetriswithlife.gameScreen.game.Vector
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GridTest {
    private lateinit var game: Game

    @Before
    fun setup() {
        val recordRepository = mockk<RecordRepository>()
        game = Game(
            width = 5,
            height = 10,
            recordRepository
        )
    }

    @Test
    fun whenPointIsInside_shouldReturnTrue() {
        val result = game.grid.isInside(Vector(1, 1))

        assertTrue(result)
    }

    @Test
    fun whenPointIsNotInside_shouldReturnFalse() {
        val result = game.grid.isInside(Vector(7, 1))

        assertFalse(result)
    }

    @Test
    fun whenPointIsOutside_shouldReturnTrue() {
        val result = game.grid.isOutside(Vector(7, 1))

        assertTrue(result)
    }

    @Test
    fun whenPointIsNotOutside_shouldReturnFalse() {
        val result = game.grid.isOutside(Vector(1, 1))

        assertFalse(result)
    }

    @Test
    fun whenPointFree_shouldReturnTrue() {
        val result = game.grid.isFree(Vector(1, 1))

        assertTrue(result)
    }

    @Test
    fun whenPointNotFree_shouldReturnFalse() {
        game.grid.space[1][1].setBlock(1)
        val result = game.grid.isFree(Vector(1, 1))

        assertFalse(result)
    }

    @Test
    fun whenPointNotFree_shouldReturnTrue() {
        game.grid.space[1][1].setBlock(1)
        val result = game.grid.isNotFree(Vector(1, 1))
        assertTrue(result)
    }

    @Test
    fun whenPointFree_shouldReturnFalse() {
        val result = game.grid.isNotFree(Vector(1, 1))

        assertFalse(result)
    }

    @Test
    fun whenRowNotFull_shouldReturnZero() {
        val result = game.grid.countRowFull

        assertEquals(0, result)
    }

    @Test
    fun whenRowFullOne_shouldReturnOne() {
        for (i in 0 until 5)
            game.grid.space[9][i].setBlock(1)
        val result = game.grid.countRowFull

        assertEquals(1, result)
    }

    @Test
    fun whenRowFullTwo_shouldReturnTwo() {
        for (i in 0 until 5) {
            game.grid.space[5][i].setBlock(1)
            game.grid.space[9][i].setBlock(1)
        }
        val result = game.grid.countRowFull

        assertEquals(2, result)
    }

    @Test
    fun whenRowFull_shouldCheckRowClean() {
        for (i in 0 until 5)
            game.grid.space[9][i].setBlock(1)
        game.grid.removeRows()

        assertEquals(0, game.grid.space[9][0].block)
    }
}