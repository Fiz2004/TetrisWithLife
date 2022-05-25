package com.fiz.tetriswithlife.gameScreen.domain.models

import com.fiz.tetriswithlife.gameScreen.domain.repositories.RecordRepository
import com.fiz.tetriswithlife.gameScreen.game.Coordinate
import com.fiz.tetriswithlife.gameScreen.game.Game
import com.fiz.tetriswithlife.gameScreen.game.Grid
import com.fiz.tetriswithlife.gameScreen.game.character.Character
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test

class CharacterTest {

    @Test
    fun whenNoWayUp_shouldNoFindWay() {
        val recordRepository = mockk<RecordRepository>()
        val game = Game(13, 25, recordRepository)
        game.grid.space[12][0].block = 1
        game.grid.space[12][1].block = 1
        game.grid.space[12][2].block = 1
        game.grid.space[13][2].block = 1
        game.grid.space[13][3].block = 1
        game.grid.space[14][3].block = 1
        game.grid.space[14][4].block = 1
        game.grid.space[14][5].block = 1
        game.grid.space[15][5].block = 1
        game.grid.space[16][5].block = 1
        game.grid.space[16][6].block = 1
        game.grid.space[16][7].block = 1
        game.grid.space[17][7].block = 1
        game.grid.space[18][7].block = 1
        game.grid.space[19][7].block = 1
        game.grid.space[19][6].block = 1
        game.grid.space[20][7].block = 1
        game.grid.space[20][8].block = 1
        game.grid.space[20][9].block = 1
        game.grid.space[21][9].block = 1
        game.grid.space[21][10].block = 1
        game.grid.space[22][10].block = 1
        game.grid.space[23][10].block = 1
        game.grid.space[24][10].block = 1
        game.grid.space[21][11].block = 1
        game.grid.space[21][12].block = 1
        val character = Character.create(
            game.grid,
            Coordinate(5.0, 24.0)
        )

        val isPathUp = game.isPathUp(
            character.position.posTile,
            game.getFullCopySpace()
        )

        Assert.assertFalse(isPathUp)
    }

    @Test
    fun moveCharacter() {
        val grid: Grid = Grid.create(13, 15)

        grid.space[24][5].block = 1
        grid.space[23][5].block = 1
        grid.space[24][7].block = 1
        grid.space[23][7].block = 1
        grid.space[24][8].block = 1
        grid.space[24][4].block = 1

        val character: Character = Character.create(grid, coordinate = Coordinate(5.0, 22.0))
    }
}