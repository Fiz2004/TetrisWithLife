package com.fiz.tetriswithlife

import com.fiz.tetriswithlife.gameScreen.game.Grid
import com.fiz.tetriswithlife.gameScreen.game.Vector
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ActorsTest {
    private lateinit var grid: Grid

    @Before
    fun setup() {
        grid = Grid(
            width = 5,
            height = 10,
        )
    }

    @Test
    fun whenPointIsInside_shouldReturnTrue() {
        val result = grid.isInside(Vector(1, 1))

        assertTrue(result)
    }

    @Test
    fun whenPointIsNotInside_shouldReturnFalse() {
        val result = grid.isInside(Vector(7, 1))

        assertFalse(result)
    }

    @Test
    fun whenPointIsOutside_shouldReturnTrue() {
        val result = grid.isOutside(Vector(7, 1))

        assertTrue(result)
    }

    @Test
    fun whenPointIsNotOutside_shouldReturnFalse() {
        val result = grid.isOutside(Vector(1, 1))

        assertFalse(result)
    }

    @Test
    fun whenPointFree_shouldReturnTrue() {
        val result = grid.isFree(Vector(1, 1))

        assertTrue(result)
    }

    @Test
    fun whenPointNotFree_shouldReturnFalse() {
        grid.space[1][1].setBlock(1)
        val result = grid.isFree(Vector(1, 1))

        assertFalse(result)
    }

    @Test
    fun whenPointNotFree_shouldReturnTrue() {
        grid.space[1][1].setBlock(1)
        val result = grid.isNotFree(Vector(1, 1))
        assertTrue(result)
    }

    @Test
    fun whenPointFree_shouldReturnFalse() {
        val result = grid.isNotFree(Vector(1, 1))

        assertFalse(result)
    }

    @Test
    fun whenRowNotFull_shouldReturnZero() {
        val result = grid.countRowFull

        assertEquals(0, result)
    }

    @Test
    fun whenRowFullOne_shouldReturnOne() {
        for (i in 0 until 5)
            grid.space[9][i].setBlock(1)
        val result = grid.countRowFull

        assertEquals(1, result)
    }

    @Test
    fun whenRowFullTwo_shouldReturnTwo() {
        for (i in 0 until 5) {
            grid.space[5][i].setBlock(1)
            grid.space[9][i].setBlock(1)
        }
        val result = grid.countRowFull

        assertEquals(2, result)
    }

    @Test
    fun whenRowFull_shouldCheckRowClean() {
        for (i in 0 until 5)
            grid.space[9][i].setBlock(1)
        grid.removeRows()

        assertEquals(0, grid.space[9][0].block)
    }
}