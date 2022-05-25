package com.fiz.tetriswithlife.gameScreen.domain.useCase

import com.fiz.tetriswithlife.gameScreen.domain.repositories.RecordRepository
import com.fiz.tetriswithlife.gameScreen.game.Coordinate
import com.fiz.tetriswithlife.gameScreen.game.Game
import com.fiz.tetriswithlife.gameScreen.game.character.Character
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MovementTest {

    lateinit var game: Game
    lateinit var character: Character

    @Before
    fun setUp() {
        val recordRepository = mockk<RecordRepository>()
        val game = Game(10, 10, recordRepository)
        character = Character.create(game.grid, Coordinate(5.0, 9.0))
    }

    @Test
    fun whenCharacterCanDirectionsFreeNoEat_shouldReturnTrue() {
        val directions = listOf(Character.Companion.Direction.Left)

        val canMove =
            game.isCanDirectionsAndSetCharacterEat(directions, false)
        assertTrue(canMove)
        assertFalse(character.eat)
    }

    @Test
    fun whenCharacterCanDirectionsNotFreeNoEat_shouldReturnFalse() {
        val directions = listOf(Character.Companion.Direction.Left)
        game.grid.space[9][4].block = 5

        val canMove = game.isCanDirectionsAndSetCharacterEat(
            directions,
            false
        )

        assertFalse(canMove)
        assertFalse(character.eat)
    }

    @Test
    fun whenCharacterCanDirectionsNotFreeCanEat_shouldReturnTrue() {
        val directions = listOf(Character.Companion.Direction.Left)
        game.grid.space[9][4].block = 5

        val canMove =
            game.isCanDirectionsAndSetCharacterEat(
                directions,
                true
            )

        assertTrue(canMove)
        assertTrue(character.eat)
    }
}