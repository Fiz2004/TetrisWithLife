package com.fiz.tetriswithlife

import com.fiz.tetriswithlife.grid.Element
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName

@DisplayName("ElementTest")
class ElementTest {
    var element= Element(0,0,mutableMapOf('L' to 1, 'R' to 2, 'U' to 3))

    @BeforeEach
    fun init() {
        element= Element(0,0,mutableMapOf('L' to 1, 'R' to 2, 'U' to 3))
    }

    @Test
    @DisplayName("getSpaceStatus")
    fun getSpaceStatus() {
        val result=element.getSpaceStatus()
        assertEquals(result, 'L')
    }

    @Test
    @DisplayName("setZero")
    fun setZero() {
        element.setZero()

        assertEquals(element.block, 0)
        assertEquals(element.status['L'], 0)
        assertEquals(element.status['R'], 0)
        assertEquals(element.status['U'], 0)
    }

    @Test
    @DisplayName("setElement")
    fun setElement() {
        element.setElement(Element(5,2,mutableMapOf('L' to 2, 'R' to 1, 'U' to 4)))
        assertEquals(element.block, 2)
        assertEquals(element.status['L'], 2)
        assertEquals(element.status['R'], 1)
        assertEquals(element.status['U'], 4)
    }
}