package com.fiz.tetriswithlife.gameScreen.domain.useCase

import com.fiz.tetriswithlife.gameScreen.domain.models.Coordinate
import com.fiz.tetriswithlife.gameScreen.domain.models.Game
import com.fiz.tetriswithlife.gameScreen.domain.models.Grid
import com.fiz.tetriswithlife.gameScreen.domain.models.Vector
import com.fiz.tetriswithlife.gameScreen.domain.models.character.Character
import com.fiz.tetriswithlife.gameScreen.domain.models.character.Location
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MovementTest {

    lateinit var game: Game
    lateinit var character: Character

    @Before
    fun setUp() {
        game = Game(Grid(10, 10))
        character = Character(Location(Coordinate(5.0, 9.0)))
    }

    @Test
    fun whenCharacterCanDirectionsFreeNoEat_shouldReturnTrue() {
        val directions = listOf(Vector(-1, 0))

        val canMove = character.movement.isCanDirectionsAndSetCharacterEat(
            character.location.position.posTile,
            directions,
            game,
            false,
            {}
        )

        assertTrue(canMove)
        assertFalse(character.eat)
    }

    @Test
    fun whenCharacterCanDirectionsNotFreeNoEat_shouldReturnFalse() {
        val directions = listOf(Vector(-1, 0))
        game.grid.space[9][4].block = 5

        val canMove = character.movement.isCanDirectionsAndSetCharacterEat(
            character.location.position.posTile,
            directions,
            game,
            false,
            {}
        )

        assertFalse(canMove)
        assertFalse(character.eat)
    }

    @Test
    fun whenCharacterCanDirectionsNotFreeCanEat_shouldReturnTrue() {
        val directions = listOf(Vector(-1, 0))
        game.grid.space[9][4].block = 5

        val canMove = character.movement.isCanDirectionsAndSetCharacterEat(
            character.location.position.posTile,
            directions,
            game,
            true,
            { character.eat = true }
        )

        assertTrue(canMove)
        assertTrue(character.eat)
    }
}