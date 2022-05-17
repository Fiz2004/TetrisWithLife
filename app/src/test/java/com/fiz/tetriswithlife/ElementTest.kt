package com.fiz.tetriswithlife

import com.fiz.tetriswithlife.game.domain.grid.Element
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ElementTest {
    private lateinit var element: Element

    @Before
    fun setup() {
        element = Element(0, 0, mutableMapOf('L' to 1, 'R' to 2, 'U' to 3))
    }

    @Test
    fun whenAllSpaceZero_shouldReturnL() {

        val result = element.getSpaceStatus()

        assertEquals('L', result)
    }

    @Test
    fun whenAllSpaceZero_shouldReturnAllZero() {
        element.setZero()

        assertEquals(0, element.block)
        assertEquals(0, element.status['L'])
        assertEquals(0, element.status['R'])
        assertEquals(0, element.status['U'])
    }

    @Test
    fun whenSetElement_shouldReturnElement() {
        element.setElement(Element(5, 2, mutableMapOf('L' to 2, 'R' to 1, 'U' to 4)))

        assertEquals(2, element.block)
        assertEquals(2, element.status['L'])
        assertEquals(1, element.status['R'])
        assertEquals(4, element.status['U'])
    }
}