package com.fiz.tetriswithlife

import com.fiz.tetriswithlife.gameScreen.game.Element
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ElementTest {
    private lateinit var element: Element

    @Before
    fun setup() {
        element = Element(0, 0, Element.Companion.StatusElement.Left(1))
    }

    @Test
    fun whenAllSpaceZero_shouldReturnL() {

        val result = element.status is Element.Companion.StatusElement.Left

        assertTrue(result)
    }

    @Test
    fun whenAllSpaceZero_shouldReturnAllZero() {
        element.setZero()
        val result = element.status is Element.Companion.StatusElement.Whole

        assertEquals(0, element.block)
        assertTrue(result)
    }

    @Test
    fun whenSetElement_shouldReturnElement() {
        val result = element.status as Element.Companion.StatusElement.Left
        val damage = result.damage

        assertEquals(0, element.block)
        assertEquals(1, damage)
    }
}