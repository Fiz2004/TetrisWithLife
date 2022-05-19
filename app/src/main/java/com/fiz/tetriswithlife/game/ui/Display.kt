package com.fiz.tetriswithlife.game.ui

import android.graphics.*
import com.fiz.tetriswithlife.game.data.BitmapRepository
import com.fiz.tetriswithlife.game.domain.grid.Element
import com.fiz.tetriswithlife.game.domain.models.Point
import kotlin.math.min


private const val NUMBER_COLUMNS_IMAGES_FON = 4
private const val NUMBER_ROWS_IMAGES_FON = 4

class Display(
    widthSurface: Int,
    heightSurface: Int,
    widthGrid: Int,
    heightGrid: Int,
    bitmapRepository: BitmapRepository
) {
    private val paint: Paint = Paint()

    private var bmpFon: Bitmap = bitmapRepository.bmpFon
    private var bmpCharacter: Bitmap = bitmapRepository.bmpCharacter
    private var bmpKv: List<Bitmap> = bitmapRepository.bmpKv

    private var tile: Int = bmpFon.width / NUMBER_COLUMNS_IMAGES_FON

    private var newTile = min(
        heightSurface / heightGrid,
        widthSurface / widthGrid
    ).toFloat()

    private var offset = Point(
        ((widthSurface - widthGrid * newTile) / 2).toInt(),
        ((heightSurface - heightGrid * newTile) / 2).toInt()
    )

    fun render(gameState: GameState, canvas: Canvas) {

        canvas.drawColor(Color.parseColor("#161616"))
        drawGridElements(gameState, canvas)
        drawCurrentFigure(gameState, canvas)
        drawCharacter(gameState, canvas)
    }

    fun renderInfo(gameState: GameState, nextFigureCanvas: Canvas) {
        drawNextFigure(gameState, nextFigureCanvas)
    }

    private fun drawGridElements(gameState: GameState, canvas: Canvas) {
        for (y in 0 until gameState.grid.height)
            for (x in 0 until gameState.grid.width) {
                val screenX = offset.x + x * newTile
                val screenY = offset.y + y * newTile
                val offsetX =
                    (gameState.grid.space[y][x].background / NUMBER_COLUMNS_IMAGES_FON) * tile
                val offsetY =
                    (gameState.grid.space[y][x].background % NUMBER_ROWS_IMAGES_FON) * tile

                canvas.drawBitmap(
                    bmpFon,
                    Rect(offsetX, offsetY, offsetX + tile, offsetY + tile),
                    RectF(screenX, screenY, screenX + newTile, screenY + newTile),
                    paint
                )
            }

        for (y in 0 until gameState.grid.height)
            for (x in 0 until gameState.grid.width)
                if (gameState.grid.space[y][x].block != 0) {
                    val screenX = offset.x + x * newTile
                    val screenY = offset.y + y * newTile
                    val offset: Point = getOffset(gameState.grid.space[y][x])

                    canvas.drawBitmap(
                        bmpKv[gameState.grid.space[y][x].block - 1],
                        Rect(
                            offset.x * tile,
                            offset.y * tile,
                            offset.x * tile + tile,
                            offset.y * tile + tile
                        ),
                        RectF(screenX, screenY, screenX + newTile, screenY + newTile),
                        paint
                    )
                }
    }

    private fun drawCurrentFigure(gameState: GameState, canvas: Canvas) {
        for (cell in gameState.currentFigure.figure.cells) {
            val screenX =
                offset.x + ((cell.point.x + gameState.currentFigure.position.x) * newTile).toFloat()
            val screenY =
                offset.y + ((cell.point.y + gameState.currentFigure.position.y) * newTile).toFloat()
            var oldY = 0
            var cY = screenY
            var nTile = newTile
            if (screenY - offset.y < 0 && screenY + newTile - offset.y < 0) return
            if (screenY - offset.y < 0) {
                nTile = screenY - offset.y + newTile
                oldY = (nTile * tile / newTile).toInt()
                cY = offset.y.toFloat()
            }
            canvas.drawBitmap(
                bmpKv[cell.view - 1],
                Rect(0, oldY, tile, tile),
                RectF(screenX, cY, screenX + newTile, cY + nTile),
                paint
            )
        }
    }

    private fun drawCharacter(gameState: GameState, canvas: Canvas) {
        val offset = gameState.character.getSprite() * tile
        val screenX = this.offset.x + (gameState.character.position.x * newTile).toFloat()
        val screenY = this.offset.y + (gameState.character.position.y * newTile).toFloat()
        canvas.drawBitmap(
            bmpCharacter,
            Rect(
                offset.x,
                offset.y,
                offset.x + tile,
                offset.y + tile
            ),
            RectF(screenX, screenY, screenX + newTile, screenY + newTile),
            paint
        )
    }

    private fun drawNextFigure(gameState: GameState, canvasInfo: Canvas) {
        val offset = Point(
            ((canvasInfo.width - 4 * newTile) / 2).toInt(),
            ((canvasInfo.height - 4 * newTile) / 2).toInt()
        )
        canvasInfo.drawColor(Color.parseColor("#242424"))
        for (cell in gameState.nextFigure.cells) {
            val screenX = offset.x + (cell.point.x) * newTile
            val screenY = offset.y + (cell.point.y) * newTile
            canvasInfo.drawBitmap(
                bmpKv[cell.view - 1],
                Rect(0, 0, tile, tile),
                RectF(screenX, screenY, screenX + newTile, screenY + newTile),
                paint
            )
        }
    }
}

fun getOffset(element: Element): Point {
    if (element.getSpaceStatus() == 'R')
        return Point(((element.status['R'] ?: (0 - 1))), 1)

    if (element.getSpaceStatus() == 'L')
        return Point(((element.status['L'] ?: (0 - 1))), 2)

    if (element.getSpaceStatus() == 'U')
        return Point(((element.status['U'] ?: (0 - 1))), 3)

    return Point(0, 0)
}
