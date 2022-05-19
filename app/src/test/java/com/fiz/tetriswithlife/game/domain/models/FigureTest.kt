package com.fiz.tetriswithlife.game.domain.models

import org.junit.Assert
import org.junit.Test

class FigureTest {

    @Test
    fun whenFigureWithMaxXEquals3_shouldReturn3() {
        val figure = Figure(getNumberFigure = 0)

        val result = figure.getMaxX()

        Assert.assertEquals(3, result)
    }

    @Test
    fun whenFigureWithMaxYEquals1_shouldReturn1() {
        val figure = Figure(getNumberFigure = 0)

        val result = figure.getMaxY()

        Assert.assertEquals(1, result)
    }
}