package com.fiz.tetriswithlife.gameScreen.ui

import android.graphics.*
import com.fiz.tetriswithlife.gameScreen.data.BitmapRepository
import com.fiz.tetriswithlife.gameScreen.domain.models.Element
import com.fiz.tetriswithlife.gameScreen.domain.models.Vector
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

    private var offset = Vector(
        ((widthSurface - widthGrid * newTile) / 2).toInt(),
        ((heightSurface - heightGrid * newTile) / 2).toInt()
    )

    fun render(viewState: ViewState, canvas: Canvas, color: Int) {

        canvas.drawColor(color)
        drawGridElements(viewState, canvas)
        drawCurrentFigure(viewState, canvas)
        drawCharacter(viewState, canvas)
    }

    fun renderInfo(viewState: ViewState, nextFigureCanvas: Canvas, color: Int) {
        drawNextFigure(viewState, nextFigureCanvas, color)
    }

    private fun drawGridElements(viewState: ViewState, canvas: Canvas) {
        for (y in 0 until viewState.gameState.grid.height)
            for (x in 0 until viewState.gameState.grid.width) {
                val screenX = offset.x + x * newTile
                val screenY = offset.y + y * newTile
                val offsetX =
                    (viewState.gameState.grid.space[y][x].background / NUMBER_COLUMNS_IMAGES_FON) * tile
                val offsetY =
                    (viewState.gameState.grid.space[y][x].background % NUMBER_ROWS_IMAGES_FON) * tile

                canvas.drawBitmap(
                    bmpFon,
                    Rect(offsetX, offsetY, offsetX + tile, offsetY + tile),
                    RectF(screenX, screenY, screenX + newTile, screenY + newTile),
                    paint
                )
            }

        for (y in 0 until viewState.gameState.grid.height)
            for (x in 0 until viewState.gameState.grid.width)
                if (viewState.gameState.grid.space[y][x].block != 0) {
                    val screenX = offset.x + x * newTile
                    val screenY = offset.y + y * newTile
                    val offset: Vector = getOffset(viewState.gameState.grid.space[y][x])

                    canvas.drawBitmap(
                        bmpKv[viewState.gameState.grid.space[y][x].block - 1],
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

    private fun drawCurrentFigure(viewState: ViewState, canvas: Canvas) {
        for (cell in viewState.gameState.currentFigure.figure.cells) {
            val screenX =
                offset.x + ((cell.vector.x + viewState.gameState.currentFigure.position.x) * newTile).toFloat()
            val screenY =
                offset.y + ((cell.vector.y + viewState.gameState.currentFigure.position.y) * newTile).toFloat()
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

    private fun drawCharacter(viewState: ViewState, canvas: Canvas) {
        val offset = viewState.gameState.character.getSprite() * tile
        val screenX =
            this.offset.x + (viewState.gameState.character.location.position.x * newTile).toFloat()
        val screenY =
            this.offset.y + (viewState.gameState.character.location.position.y * newTile).toFloat()
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

    private fun drawNextFigure(viewState: ViewState, canvasInfo: Canvas, color: Int) {

        val oneTile = min(
            canvasInfo.width / 4,
            canvasInfo.height / 4
        ).toFloat()

        val x = viewState.gameState.nextFigure.getWidth()
        val ostX = 4 - x
        val porX = ostX / 2
        val pixX = porX * oneTile

        val y = viewState.gameState.nextFigure.getHeight()
        val ostY = 4 - y
        val porY = ostY / 2
        val pixY = porY * oneTile

        val offset =
            Vector(pixX.toInt(), pixY.toInt())

        canvasInfo.drawColor(color)


        for (cell in viewState.gameState.nextFigure.cells) {
            val screenX = offset.x + (cell.vector.x) * oneTile
            val screenY = offset.y + (cell.vector.y) * oneTile
            canvasInfo.drawBitmap(
                bmpKv[cell.view - 1],
                Rect(0, 0, tile, tile),
                RectF(screenX, screenY, screenX + oneTile, screenY + oneTile),
                paint
            )
        }
    }
}

fun getOffset(element: Element): Vector {
    if (element.getSpaceStatus() == 'R')
        return Vector(((element.status['R'] ?: (0 - 1))), 1)

    if (element.getSpaceStatus() == 'L')
        return Vector(((element.status['L'] ?: (0 - 1))), 2)

    if (element.getSpaceStatus() == 'U')
        return Vector(((element.status['U'] ?: (0 - 1))), 3)

    return Vector(0, 0)
}
