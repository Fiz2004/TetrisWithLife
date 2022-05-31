package com.fiz.tetriswithlife.gameScreen.domain

import android.graphics.Rect
import android.graphics.RectF
import com.fiz.tetriswithlife.gameScreen.game.Element
import com.fiz.tetriswithlife.gameScreen.game.Game
import com.fiz.tetriswithlife.gameScreen.game.Grid
import com.fiz.tetriswithlife.gameScreen.game.Vector
import com.fiz.tetriswithlife.gameScreen.game.character.Character
import com.fiz.tetriswithlife.gameScreen.game.figure.CurrentFigure
import com.fiz.tetriswithlife.gameScreen.game.figure.Figure
import com.fiz.tetriswithlife.gameScreen.ui.GameState
import com.fiz.tetriswithlife.gameScreen.ui.models.*

private const val NUMBER_COLUMNS_IMAGES_FON = 4
private const val NUMBER_ROWS_IMAGES_FON = 4


class RefreshGameStateFromGame(
    var tile: Int = 0,
    var newTile: Float = 0f,
    var offset: Vector = Vector(0, 0),
    var oneTileInfo: Float = 0f
) {
    operator fun invoke(game: Game): GameState {
        return GameState(
            backgroundsUi = getBackgroundsUi(game.grid),
            blocksUi = getBlocksUi(game.grid),
            characterUi = getCharacterUi(game.character),
            blocksCurrentFigureUi = getCurrentFigure(game.currentFigure),
            blocksNextFigureUi = getNextFigureUi(game.nextFigure),
            scores = game.scores,
        )
    }

    private fun getBackgroundsUi(grid: Grid): List<BackgroundUi> {
        val result = mutableListOf<BackgroundUi>()
        for (y in 0 until grid.height) {
            for (x in 0 until grid.width) {
                val screenX = offset.x + x * newTile
                val screenY = offset.y + y * newTile
                val offsetX =
                    (grid.space[y][x].background / NUMBER_COLUMNS_IMAGES_FON) * tile
                val offsetY =
                    (grid.space[y][x].background % NUMBER_ROWS_IMAGES_FON) * tile

                result.add(
                    BackgroundUi(
                        src = Rect(offsetX, offsetY, offsetX + tile, offsetY + tile),
                        dst = RectF(screenX, screenY, screenX + newTile, screenY + newTile)
                    )
                )
            }
        }
        return result
    }

    private fun getBlocksUi(grid: Grid): List<BlockUi> {
        val result = mutableListOf<BlockUi>()
        for (y in 0 until grid.height) {
            for (x in 0 until grid.width) {
                if (grid.space[y][x].block != 0) {
                    val screenX = offset.x + x * newTile
                    val screenY = offset.y + y * newTile
                    val offset: Vector = getOffset(grid.space[y][x])

                    result.add(
                        BlockUi(
                            value = grid.space[y][x].block - 1,
                            src = Rect(
                                offset.x * tile,
                                offset.y * tile,
                                offset.x * tile + tile,
                                offset.y * tile + tile
                            ),
                            dst = RectF(screenX, screenY, screenX + newTile, screenY + newTile)
                        )
                    )
                }
            }
        }
        return result
    }

    private fun getOffset(element: Element): Vector {
        if (element.getSpaceStatus() == 'R')
            return Vector(((element.status['R'] ?: (0 - 1))), 1)

        if (element.getSpaceStatus() == 'L')
            return Vector(((element.status['L'] ?: (0 - 1))), 2)

        if (element.getSpaceStatus() == 'U')
            return Vector(((element.status['U'] ?: (0 - 1))), 3)

        return Vector(0, 0)
    }

    private fun getCharacterUi(character: Character): CharacterUi {
        val offset = character.getSprite() * tile
        val screenX =
            this.offset.x + (character.location.position.x * newTile).toFloat()
        val screenY =
            this.offset.y + (character.location.position.y * newTile).toFloat()

        return CharacterUi(
            src = Rect(
                offset.x,
                offset.y,
                offset.x + tile,
                offset.y + tile
            ),
            dst = RectF(screenX, screenY, screenX + newTile, screenY + newTile),
            breath = character.breath.breath,
            secondsSupplyForBreath = character.breath.secondsSupplyForBreath
        )
    }

    private fun getCurrentFigure(currentFigure: CurrentFigure): List<CurrentFigureUi> {
        val result = mutableListOf<CurrentFigureUi>()

        for (cell in currentFigure.figure.cells) {
            val screenX =
                offset.x + ((cell.vector.x + currentFigure.position.x) * newTile).toFloat()
            val screenY =
                offset.y + ((cell.vector.y + currentFigure.position.y) * newTile).toFloat()
            var oldY = 0
            var cY = screenY
            var nTile = newTile
            if (screenY - offset.y < 0 && screenY + newTile - offset.y < 0) continue
            if (screenY - offset.y < 0) {
                nTile = screenY - offset.y + newTile
                oldY = (nTile * tile / newTile).toInt()
                cY = offset.y.toFloat()
            }

            result.add(
                CurrentFigureUi(
                    value = cell.view - 1,
                    src = Rect(0, oldY, tile, tile),
                    dst = RectF(screenX, cY, screenX + newTile, cY + nTile)
                )
            )
        }

        return result
    }

    private fun getNextFigureUi(nextFigure: Figure): List<NextFigureUi> {

        val result = mutableListOf<NextFigureUi>()

        val x = nextFigure.getWidth()
        val ostX = 4 - x
        val porX = ostX / 2
        val pixX = porX * oneTileInfo

        val y = nextFigure.getHeight()
        val ostY = 4 - y
        val porY = ostY / 2
        val pixY = porY * oneTileInfo

        val offset =
            Vector(pixX.toInt(), pixY.toInt())

        for (cell in nextFigure.cells) {
            val screenX = offset.x + (cell.vector.x) * oneTileInfo
            val screenY = offset.y + (cell.vector.y) * oneTileInfo

            result.add(
                NextFigureUi(
                    value = cell.view - 1,
                    src = Rect(0, 0, tile, tile),
                    dst = RectF(screenX, screenY, screenX + oneTileInfo, screenY + oneTileInfo)
                )
            )
        }

        return result
    }
}