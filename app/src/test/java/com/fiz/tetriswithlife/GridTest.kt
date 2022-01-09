package com.fiz.tetriswithlife

import com.fiz.tetriswithlife.grid.Grid
import com.fiz.tetriswithlife.grid.Point
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("GridTest")
internal class GridTest {
    private lateinit var grid:Grid

    @BeforeEach
    fun setUp() {
        grid= Grid(5,10) { 0 }
    }


    @Test
    @DisplayName("isInside")
    fun isInside() {
        var result=grid.isInside(Point(1,1))
        assertTrue(result)
        result=grid.isInside(Point(0,0))
        assertTrue(result)
        result=grid.isInside(Point(7,1))
        assertFalse(result)
        result=grid.isInside(Point(5,10))
        assertFalse(result)
    }

    @Test
    @DisplayName("isOutside")
    fun isOutside() {
        var result=grid.isOutside(Point(1,1))
        assertFalse(result)
        result=grid.isOutside(Point(0,0))
        assertFalse(result)
        result=grid.isOutside(Point(7,1))
        assertTrue(result)
        result=grid.isOutside(Point(5,10))
        assertTrue(result)
    }

    @Test
    @DisplayName("isFree")
    fun isFree() {
        var result=grid.isFree(Point(1,1))
        assertTrue(result)
        grid.space[1][1].block=1
        result=grid.isFree(Point(1,1))
        assertFalse(result)
    }

    @Test
    @DisplayName("isNotFree")
    fun isNotFree() {
        var result=grid.isNotFree(Point(1,1))
        assertFalse(result)
        grid.space[1][1].block=1
        result=grid.isNotFree(Point(1,1))
        assertTrue(result)
    }

    @Test
    @DisplayName("getCountRowFull")
    fun getCountRowFull() {
        var result=grid.getCountRowFull()
        assertEquals(result,0)

        for (i in 0 until 5)
            grid.space[9][i].block=1
        result=grid.getCountRowFull()
        assertEquals(result,1)

        for (i in 0 until 5)
            grid.space[5][i].block=1
        result=grid.getCountRowFull()
        assertEquals(result,2)
    }

    @Test
    @DisplayName("deleteRows")
    fun deleteRows() {
        for (i in 0 until 5)
            grid.space[9][i].block=1
        grid.deleteRows()
        assertEquals(grid.space[9][0].block,0)

    }
}