package com.fiz.tetriswithlife.game.domain.character

import com.fiz.tetriswithlife.game.domain.grid.Coordinate
import com.fiz.tetriswithlife.game.domain.grid.Grid
import org.junit.Assert.assertFalse
import org.junit.Test

class CharacterBreathTest {

    @Test
    fun whenBreathNo_shouldNoFindWay() {
        val grid = Grid(13, 25)
        grid.space[12][0].block = 1
        grid.space[12][1].block = 1
        grid.space[12][2].block = 1
        grid.space[13][2].block = 1
        grid.space[13][3].block = 1
        grid.space[14][3].block = 1
        grid.space[14][4].block = 1
        grid.space[14][5].block = 1
        grid.space[15][5].block = 1
        grid.space[16][5].block = 1
        grid.space[16][6].block = 1
        grid.space[16][7].block = 1
        grid.space[17][7].block = 1
        grid.space[18][7].block = 1
        grid.space[19][7].block = 1
        grid.space[19][6].block = 1
        grid.space[20][7].block = 1
        grid.space[20][8].block = 1
        grid.space[20][9].block = 1
        grid.space[21][9].block = 1
        grid.space[21][10].block = 1
        grid.space[22][10].block = 1
        grid.space[23][10].block = 1
        grid.space[24][10].block = 1
        grid.space[21][11].block = 1
        grid.space[21][12].block = 1
        val character = CharacterBreath(grid)
        character.position = Coordinate(5.0, 24.0)
        val result = character.isBreath(grid)

        assertFalse(result)
    }
}