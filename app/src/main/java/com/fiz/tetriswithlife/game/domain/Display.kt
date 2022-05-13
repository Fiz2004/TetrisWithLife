package com.fiz.tetriswithlife.game.domain

import android.content.Context
import android.graphics.*
import android.view.SurfaceView
import com.fiz.tetriswithlife.R
import com.fiz.tetriswithlife.game.domain.character.TIMES_BREATH_LOSE
import com.fiz.tetriswithlife.game.domain.grid.Element
import com.fiz.tetriswithlife.game.domain.grid.Point
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

private const val NUMBER_IMAGES_FIGURE = 5
private const val NUMBER_COLUMNS_IMAGES_FON = 4
private const val NUMBER_ROWS_IMAGES_FON = 4

class Display(
    private val surface: SurfaceView,
    private val context: Context
) {
    companion object {
        interface Listener {
            fun setScoresTextView(scores: String)
            fun setRecordTextView(record: String)

            fun pauseButtonClick(status: String)
            fun infoBreathTextviewChangeVisibility(visibility: Boolean)
            fun breathTextviewChangeVisibilityAndColor(visibility: Boolean, sec: Double, color: Int)
        }
    }


    private lateinit var state: State
    private lateinit var canvas: Canvas
    private lateinit var canvasInfo: Canvas
    private lateinit var listener: Listener
    private val paint: Paint = Paint()
    private val bmpFon: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.fon)
    private val bmpCharacter: Bitmap =
        BitmapFactory.decodeResource(context.resources, R.drawable.character)
    private val bmpKv: Array<Bitmap> by lazy(::initBmpKv)
    private fun initBmpKv(): Array<Bitmap> {
        var result: Array<Bitmap> = emptyArray()
        for (i in 1..NUMBER_IMAGES_FIGURE)
            result += BitmapFactory.decodeResource(
                context.resources, context.resources.getIdentifier(
                    "kvadrat$i",
                    "drawable", context.packageName
                )
            )
        return result
    }

    private val tile = bmpFon.width / NUMBER_COLUMNS_IMAGES_FON
    private var newTile = (tile / 1.5).toFloat()
    private var offset = Point(surface.width, surface.height)

    fun render(state: State, canvas: Canvas) {
        this.state = state
        this.canvas = canvas
        canvas.drawColor(Color.parseColor("#161616"))
        newTile = min(
            surface.height / state.grid.height, surface.width / state.grid
                .width
        ).toFloat()
        offset = Point(
            ((surface.width - state.grid.width * newTile) / 2).toInt(),
            ((surface.height - state.grid
                .height * newTile) / 2).toInt()
        )
        drawGridElements()
        drawCurrentFigure()
        drawCharacter()
    }

    fun renderInfo(state: State, nextFigureCanvas: Canvas) {
        this.canvasInfo = nextFigureCanvas
        drawNextFigure()

        listener = context as Listener
        listener.setScoresTextView(state.scores.toString().padStart(6, '0'))
        listener.setRecordTextView(state.record.toString().padStart(6, '0'))

        listener.pauseButtonClick(state.status)


        if (state.status != "pause") {
            val sec: Double = if (state.character.breath)
                TIMES_BREATH_LOSE
            else
                max(state.character.timeBreath, 0.0)

            listener.infoBreathTextviewChangeVisibility(state.character.breath)
            val cl = ((floor(sec) * 255) / TIMES_BREATH_LOSE).toInt()
            listener.breathTextviewChangeVisibilityAndColor(state.character.breath, sec, cl)
        }
    }

    private fun drawGridElements() {
        for (y in 0 until state.grid.height)
            for (x in 0 until state.grid.width) {
                val screenX = offset.x + x * newTile
                val screenY = offset.y + y * newTile
                val offsetX = (state.grid.space[y][x].background / NUMBER_COLUMNS_IMAGES_FON) * tile
                val offsetY = (state.grid.space[y][x].background % NUMBER_ROWS_IMAGES_FON) * tile

                canvas.drawBitmap(
                    bmpFon,
                    Rect(offsetX, offsetY, offsetX + tile, offsetY + tile),
                    RectF(screenX, screenY, screenX + newTile, screenY + newTile),
                    paint
                )
            }

        for (y in 0 until state.grid.height)
            for (x in 0 until state.grid.width)
                if (state.grid.space[y][x].block != 0) {
                    val screenX = offset.x + x * newTile
                    val screenY = offset.y + y * newTile
                    val offset: Point = getOffset(state.grid.space[y][x])
                    canvas.drawBitmap(
                        bmpKv[state.grid.space[y][x].block - 1],
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

    private fun drawCurrentFigure() {
        for (cell in state.currentFigure.cells) {
            val screenX = offset.x + ((cell.x + state.currentFigure.position.x) * newTile).toFloat()
            val screenY = offset.y + ((cell.y + state.currentFigure.position.y) * newTile).toFloat()
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

    private fun drawCharacter() {
        val offset = state.character.getSprite()
        offset.x *= tile
        offset.y *= tile
        val screenX = this.offset.x + (state.character.position.x * newTile).toFloat()
        val screenY = this.offset.y + (state.character.position.y * newTile).toFloat()
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

    private fun drawNextFigure() {
        val offset = Point(
            ((canvasInfo.width - 4 * newTile) / 2).toInt(),
            ((canvasInfo.height - 4 * newTile) / 2).toInt()
        )
        canvasInfo.drawColor(Color.parseColor("#242424"))
        for (cell in state.nextFigure.cells) {
            val screenX = offset.x + (cell.x) * newTile
            val screenY = offset.y + (cell.y) * newTile
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
