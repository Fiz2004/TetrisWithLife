package com.fiz.tetriswithlife

import com.fiz.tetriswithlife.gameScreen.game.Game
import com.fiz.tetriswithlife.gameScreen.game.Grid
import com.fiz.tetriswithlife.gameScreen.game.Vector
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GridTest {
    private lateinit var game: Game

    @Before
    fun setup() {
        game = Game(Grid(5, 10, { 0 }))
    }

    @Test
    fun whenPointIsInside_shouldReturnTrue() {
        val result = game.isInside(Vector(1, 1))

        assertTrue(result)
    }

    @Test
    fun whenPointIsNotInside_shouldReturnFalse() {
        val result = game.isInside(Vector(7, 1))

        assertFalse(result)
    }

    @Test
    fun whenPointIsOutside_shouldReturnTrue() {
        val result = game.isOutside(Vector(7, 1))

        assertTrue(result)
    }

    @Test
    fun whenPointIsNotOutside_shouldReturnFalse() {
        val result = game.isOutside(Vector(1, 1))

        assertFalse(result)
    }

    @Test
    fun whenPointFree_shouldReturnTrue() {
        val result = game.isFree(Vector(1, 1))

        assertTrue(result)
    }

    @Test
    fun whenPointNotFree_shouldReturnFalse() {
        game.grid.space[1][1].block = 1
        val result = game.isFree(Vector(1, 1))

        assertFalse(result)
    }

    @Test
    fun whenPointNotFree_shouldReturnTrue() {
        game.grid.space[1][1].block = 1
        val result = game.isNotFree(Vector(1, 1))
        assertTrue(result)
    }

    @Test
    fun whenPointFree_shouldReturnFalse() {
        val result = game.isNotFree(Vector(1, 1))

        assertFalse(result)
    }

    @Test
    fun whenRowNotFull_shouldReturnZero() {
        val result = game.getCountRowFull()

        assertEquals(0, result)
    }

    @Test
    fun whenRowFullOne_shouldReturnOne() {
        for (i in 0 until 5)
            game.grid.space[9][i].block = 1
        val result = game.getCountRowFull()

        assertEquals(1, result)
    }

    @Test
    fun whenRowFullTwo_shouldReturnTwo() {
        for (i in 0 until 5) {
            game.grid.space[5][i].block = 1
            game.grid.space[9][i].block = 1
        }
        val result = game.getCountRowFull()

        assertEquals(2, result)
    }

    @Test
    fun whenRowFull_shouldCheckRowClean() {
        for (i in 0 until 5)
            game.grid.space[9][i].block = 1
        game.deleteRows()

        assertEquals(0, game.grid.space[9][0].block)
    }
}