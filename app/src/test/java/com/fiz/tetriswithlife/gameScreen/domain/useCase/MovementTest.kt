package com.fiz.tetriswithlife.gameScreen.domain.useCase

import com.fiz.tetriswithlife.gameScreen.game.Coordinate
import com.fiz.tetriswithlife.gameScreen.game.Grid
import com.fiz.tetriswithlife.gameScreen.game.character.Character
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MovementTest {

    lateinit var grid: Grid
    lateinit var character: Character

    @Before
    fun setUp() {
        grid = Grid(10, 10)
        character = Character.create(grid, Coordinate(5.0, 9.0))
    }

    @Test
    fun whenCharacterCanDirectionsFreeNoEat_shouldReturnTrue() {
        val directions = listOf(Character.Companion.Direction.Left)

        val canMove =
            character.isCanDirectionsAndSetCharacterEat(
                directions,
                false,
                grid::isOutside,
                grid::isNotFree
            )
        assertTrue(canMove)
        assertFalse(character.eat)
    }

    @Test
    fun whenCharacterCanDirectionsNotFreeNoEat_shouldReturnFalse() {
        val directions = listOf(Character.Companion.Direction.Left)
        grid.space[9][4].setBlock(5)

        val canMove = character.isCanDirectionsAndSetCharacterEat(
            directions,
            false,
            grid::isOutside,
            grid::isNotFree
        )

        assertFalse(canMove)
        assertFalse(character.eat)
    }

    @Test
    fun whenCharacterCanDirectionsNotFreeCanEat_shouldReturnTrue() {
        val directions = listOf(Character.Companion.Direction.Left)
        grid.space[9][4].setBlock(5)

        val canMove =
            character.isCanDirectionsAndSetCharacterEat(
                directions,
                true,
                grid::isOutside,
                grid::isNotFree
            )

        assertTrue(canMove)
        assertTrue(character.eat)
    }
}