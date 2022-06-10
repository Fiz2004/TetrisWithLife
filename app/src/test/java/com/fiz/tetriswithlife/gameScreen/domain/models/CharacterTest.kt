package com.fiz.tetriswithlife.gameScreen.domain.models

import com.fiz.tetriswithlife.gameScreen.game.Actors
import com.fiz.tetriswithlife.gameScreen.game.Coordinate
import com.fiz.tetriswithlife.gameScreen.game.Grid
import com.fiz.tetriswithlife.gameScreen.game.character.Character
import org.junit.Assert
import org.junit.Test

class CharacterTest {

    @Test
    fun whenNoWayUp_shouldNoFindWay() {
        val grid = Grid(13, 25)
        val actors = Actors(grid = grid)
        grid.space[12][0].setBlock(1)
        grid.space[12][1].setBlock(1)
        grid.space[12][2].setBlock(1)
        grid.space[13][2].setBlock(1)
        grid.space[13][3].setBlock(1)
        grid.space[14][3].setBlock(1)
        grid.space[14][4].setBlock(1)
        grid.space[14][5].setBlock(1)
        grid.space[15][5].setBlock(1)
        grid.space[16][5].setBlock(1)
        grid.space[16][6].setBlock(1)
        grid.space[16][7].setBlock(1)
        grid.space[17][7].setBlock(1)
        grid.space[18][7].setBlock(1)
        grid.space[19][7].setBlock(1)
        grid.space[19][6].setBlock(1)
        grid.space[20][7].setBlock(1)
        grid.space[20][8].setBlock(1)
        grid.space[20][9].setBlock(1)
        grid.space[21][9].setBlock(1)
        grid.space[21][10].setBlock(1)
        grid.space[22][10].setBlock(1)
        grid.space[23][10].setBlock(1)
        grid.space[24][10].setBlock(1)
        grid.space[21][11].setBlock(1)
        grid.space[21][12].setBlock(1)
        val character = Character.create(
            grid,
            Coordinate(5.0, 24.0)
        )

        val isPathUp = actors.isPathUp(
            character.position.posTile,
            grid.getFullCopySpace()
        )

        Assert.assertFalse(isPathUp)
    }

}