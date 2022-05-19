package com.fiz.tetriswithlife.game.domain.useCase

import com.fiz.tetriswithlife.game.domain.models.*
import com.fiz.tetriswithlife.game.domain.models.character.Location
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateCharacterUseCaseTest {

    private val updateCharacterUseCase = UpdateCharacterUseCase()
    lateinit var grid: Grid
    lateinit var character: Character

    @Before
    fun setUp() {
        grid = Grid(10, 10)
        character = Character(Location(Coordinate(5.0, 9.0)))
    }

    @Test
    fun whenCharacterCanDirectionsFreeNoEat_shouldReturnTrue() {
        val directions = listOf(Vector(-1, 0))

        val canMove = updateCharacterUseCase.isCanDirectionsAndSetCharacterEat(
            directions,
            grid,
            character,
            false
        )

        assertTrue(canMove)
        assertFalse(character.eat)
    }

    @Test
    fun whenCharacterCanDirectionsNotFreeNoEat_shouldReturnFalse() {
        val directions = listOf(Vector(-1, 0))
        grid.space[9][4].block = 5

        val canMove = updateCharacterUseCase.isCanDirectionsAndSetCharacterEat(
            directions,
            grid,
            character,
            false
        )

        assertFalse(canMove)
        assertFalse(character.eat)
    }

    @Test
    fun whenCharacterCanDirectionsNotFreeCanEat_shouldReturnTrue() {
        val directions = listOf(Vector(-1, 0))
        grid.space[9][4].block = 5

        val canMove = updateCharacterUseCase.isCanDirectionsAndSetCharacterEat(
            directions,
            grid,
            character,
            true
        )

        assertTrue(canMove)
        assertTrue(character.eat)
    }
}