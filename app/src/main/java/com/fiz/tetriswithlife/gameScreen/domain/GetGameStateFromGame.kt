package com.fiz.tetriswithlife.gameScreen.domain

import android.graphics.Rect
import com.fiz.tetriswithlife.gameScreen.domain.repositories.BitmapRepository
import com.fiz.tetriswithlife.gameScreen.domain.repositories.RecordRepository
import com.fiz.tetriswithlife.gameScreen.game.*
import com.fiz.tetriswithlife.gameScreen.game.character.Character
import com.fiz.tetriswithlife.gameScreen.game.figure.CurrentFigure
import com.fiz.tetriswithlife.gameScreen.game.figure.Figure
import com.fiz.tetriswithlife.gameScreen.ui.GameState
import com.fiz.tetriswithlife.gameScreen.ui.models.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.floor
import kotlin.math.min

private const val NUMBER_COLUMNS_IMAGES_FON = 4
private const val NUMBER_ROWS_IMAGES_FON = 4

private const val NUMBER_FRAMES_CHARACTER_MOVE = 5

private const val NUMBER_FRAMES_ELEMENTS = 4

@Singleton
class GetGameStateFromGame @Inject constructor(
    private val bitmapRepository: BitmapRepository,
    private val recordRepository: RecordRepository
) {

    private var tile: Int = 0
    private var newTile: Int = 0
    private var newTileInfo: Int = 0
    private var globalOffsetScreen: Vector = Vector(0, 0)

    fun initGameSurface(width: Int, height: Int) {

        tile =
            bitmapRepository.bmpFon.width / NUMBER_COLUMNS_IMAGES_FON

        newTile = min(
            height / HEIGHT_GRID,
            width / WIDTH_GRID
        )

        globalOffsetScreen = Vector(
            (width - WIDTH_GRID * newTile) / 2,
            (height - HEIGHT_GRID * newTile) / 2
        )
    }

    fun initNextFigureSurface(width: Int, height: Int) {
        newTileInfo = min(
            width / 4,
            height / 4
        )
    }

    operator fun invoke(game: Game): GameState {
        return GameState(
            backgroundsUi = getBackgroundsUi(game.grid),
            blocksUi = getBlocksUi(game.grid),
            characterUi = getCharacterUi(game.grid.character),
            blocksCurrentFigureUi = getCurrentFigure(game.grid.currentFigure),
            blocksNextFigureUi = getNextFigureUi(game.nextFigure),
            scores = game.scores,
            status = getStatusUi(game.status),
            record = recordRepository.loadRecord()
        )
    }

    private fun getStatusUi(status: Game.Companion.StatusGame): GameState.Companion.StatusCurrentGame {
        return when (status) {
            Game.Companion.StatusGame.Playing -> GameState.Companion.StatusCurrentGame.Playing
            Game.Companion.StatusGame.Pause -> GameState.Companion.StatusCurrentGame.Pause
            Game.Companion.StatusGame.NewGame -> GameState.Companion.StatusCurrentGame.Playing
        }
    }

    private fun getBackgroundsUi(grid: Grid) = grid.space
        .flatMapIndexed { y, elements ->
            elements.mapIndexed { x, element ->
                val screen = globalOffsetScreen + Vector(x, y) * newTile
                val offset = Vector(
                    x = (element.background / NUMBER_COLUMNS_IMAGES_FON) * tile,
                    y = (element.background % NUMBER_ROWS_IMAGES_FON) * tile
                )

                BackgroundUi(
                    src = squareToRect(offset, tile),
                    dst = squareToRect(screen, newTile)
                )
            }
        }

    private fun getBlocksUi(grid: Grid) = grid.space
        .flatMapIndexed { y, elements ->
            elements.mapIndexedNotNull { x, element ->
                if (element.block != 0) {
                    val screen = globalOffsetScreen + Vector(x, y) * newTile
                    val offset = getOffset(element.status) * tile

                    BlockUi(
                        value = element.block - 1,
                        src = squareToRect(offset, tile),
                        dst = squareToRect(screen, newTile)
                    )
                } else {
                    null
                }
            }
        }

    private fun getOffset(status: Element.Companion.StatusElement): Vector {
        val numberSprite = getNumberSprite(status) + 1

        return when (status) {
            is Element.Companion.StatusElement.Whole -> Vector(0, 0)
            is Element.Companion.StatusElement.Right -> Vector(numberSprite, 1)
            is Element.Companion.StatusElement.Left -> Vector(numberSprite, 2)
            is Element.Companion.StatusElement.Up -> Vector(numberSprite, 3)
        }
    }

    private fun getNumberSprite(status: Element.Companion.StatusElement): Int {
        if (status is Element.Companion.StatusElement.Left)
            return (status.damage * NUMBER_FRAMES_ELEMENTS).toInt()

        if (status is Element.Companion.StatusElement.Right)
            return 3 - (status.damage * NUMBER_FRAMES_ELEMENTS).toInt()

        return 0
    }

    private fun getCharacterUi(character: Character): CharacterUi {
        val offset = getSprite(character) * tile
        val screen = globalOffsetScreen + (character.position * newTile).toPoint

        return CharacterUi(
            src = squareToRect(offset, tile),
            dst = squareToRect(screen, newTile),
            breath = character.breath.breath,
            secondsSupplyForBreath = character.breath.secondsSupplyForBreath
        )
    }


    private fun getSprite(character: Character): Vector {
        val frameX = getFrame(character.position.x)
        val frameY = getFrame(character.position.y)

        if (character.speed.isMove) {

            if (character.angle.isRight)
                return if (frameX == -1)
                    Vector(2, 0)
                else
                    if (character.eat)
                        Vector(frameX, 5)
                    else
                        Vector(frameX, 1)

            if (character.angle.isLeft)
                return if (frameX == -1)
                    Vector(6, 0)
                else
                    if (character.eat)
                        Vector(4 - frameX, 6)
                    else
                        Vector(4 - frameX, 2)



            if (character.angle.isDown)
                return if (frameY == -1)
                    Vector(0, 0)
                else
                    if (character.eat)
                        Vector(frameY, 8)
                    else
                        Vector(frameY, 4)


            if (character.angle.isUp)
                return if (frameY == -1)
                    Vector(4, 0)
                else
                    if (character.eat)
                        Vector(frameY, 7)
                    else
                        Vector(frameY, 3)
        }

        if (character.speed.isRotated) {
            if (character.angle.isRight)
                return Vector(2, 0)
            if (character.angle.isRightDown)
                return Vector(1, 0)
            if (character.angle.isDown)
                return Vector(0, 0)
            if (character.angle.isLeftDown)
                return Vector(7, 0)
            if (character.angle.isLeft)
                return Vector(6, 0)
            if (character.angle.isLeftUp)
                return Vector(5, 0)
            if (character.angle.isUp)
                return Vector(4, 0)
            if (character.angle.isRightUp)
                return Vector(3, 0)
        }

        return Vector(0, 0)
    }

    private fun getFrame(coordinate: Double): Int {
        val deviantLine = 1.0 / 100

        if (coordinate % 1 in (deviantLine..1 - deviantLine))
            return floor((coordinate % 1) * NUMBER_FRAMES_CHARACTER_MOVE).toInt()

        return -1
    }

    private fun getCurrentFigure(currentFigure: CurrentFigure) = currentFigure.figure.cells
        .mapNotNull { cell ->
            val cellPosition = ((cell.vector + currentFigure.position) * newTile).toPoint
            if (cellPosition.y < 0 && cellPosition.y + newTile < 0)
                return@mapNotNull null

            val screen = globalOffsetScreen + cellPosition

            val newStartY = if (cellPosition.y < 0) globalOffsetScreen.y else screen.y
            val newHeight =
                if (cellPosition.y < 0) newTile - (globalOffsetScreen.y - screen.y) else newTile

            val oldStartY = if (cellPosition.y < 0) (newTile - newHeight) * (tile / newTile) else 0

            CurrentFigureUi(
                value = cell.block - 1,
                src = Rect(0, oldStartY, tile, tile),
                dst = Rect(screen.x, newStartY, screen.x + newTile, newStartY + newHeight)
            )
        }

    private fun getNextFigureUi(nextFigure: Figure) = nextFigure.cells.map { cell ->
        val screen = cell.vector * newTileInfo

        NextFigureUi(
            value = cell.block - 1,
            src = squareToRect(Vector(0, 0), tile),
            dst = squareToRect(screen, newTileInfo),
        )
    }

    private fun squareToRect(offset: Vector, length: Int) =
        Rect(offset.x, offset.y, offset.x + length, offset.y + length)
}