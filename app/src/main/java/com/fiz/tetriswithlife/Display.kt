package com.fiz.tetriswithlife

import android.app.PendingIntent.getActivity
import android.bluetooth.BluetoothClass
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.*
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible


private const val NUMBER_IMAGES_FIGURE = 4
private const val SIZE_TILES = 30
private const val TIMES_BREATH_LOSE = 60
private const val NUMBER_COLUMNS_IMAGES_FON = 4
private const val NUMBER_ROWS_IMAGES_FON = 4

class Display(
    val resources: Resources, widthCanvas: Int, heightCanvas: Int,
    val scoresTextView: TextView,
    val settings: SharedPreferences,
    val recordTextView: TextView,
    val infoBreathTextview: TextView,
    val breathTextview: TextView,
    val pauseButton: Button,
    val context: Context
) {

    private val paint: Paint = Paint()

    private val bmpFon: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.fon)
    private val bmpCharacter: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.character)
    private val bmpKv: Array<Bitmap> = arrayOf(
        BitmapFactory.decodeResource(resources, R.drawable.kvadrat1),
        BitmapFactory.decodeResource(resources, R.drawable.kvadrat2),
        BitmapFactory.decodeResource(resources, R.drawable.kvadrat3),
        BitmapFactory.decodeResource(resources, R.drawable.kvadrat4),
        BitmapFactory.decodeResource(resources, R.drawable.kvadrat5)
    )

    fun drawNextFigure(nextFigure: Figure, canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        val tile = bmpFon.width / 4
        val newTile = (tile / 1.5).toFloat()
        for (cell in nextFigure.cells) {
            val screenX = (cell.x) * newTile
            val screenY = (cell.y) * newTile
            canvas.drawBitmap(
                bmpKv[cell.view - 1],
                Rect(0, 0, tile, tile),
                RectF(screenX, screenY, screenX + newTile, screenY + newTile),
                paint
            )
        }
    }

    fun render(state: State, canvas: Canvas, nextFigureCanvas: Canvas) {
        drawGridElements(state.grid, canvas)
        drawCurrentFigure(state.currentFigure, canvas)
        drawCharacter(state.character, canvas)
        drawNextFigure(state.nextFigure, nextFigureCanvas);

        scoresTextView.text = "${resources.getString(R.string.scores_game_textview)}: ${
            state.scores.toString().padStart(6, '0')
        }"
        recordTextView.text = "${resources.getString(R.string.record_game_textview)}: ${
            state.record.toString().padStart(6, '0')
        }"

        if (state.status == "pause")
            pauseButton.text = resources.getString(R.string.resume_game_button)
        else
            pauseButton.text = resources.getString(R.string.pause_game_button)

        if (state.status != "pause") {
            val sec: Int
            if (!state.character.breath)
                sec = Math.max(
                    TIMES_BREATH_LOSE - Math.ceil(
                        (System.currentTimeMillis().toDouble() - state.character
                            .timeBreath) / 1000
                    ),
                    0.0
                ).toInt()
            else
                sec = TIMES_BREATH_LOSE

            val executor = ContextCompat.getMainExecutor(context)

            if (!state.character.breath) {
                if (!breathTextview.isVisible) {
                    executor.execute(Runnable {
                        infoBreathTextview.visibility = View.VISIBLE
                        breathTextview.visibility = View.VISIBLE
                    })
                }
                executor.execute(Runnable {
                    breathTextview.text = "$sec"
                })
            } else if (breathTextview.isVisible) {
                executor.execute(Runnable {
                    infoBreathTextview.visibility = View.INVISIBLE
                    breathTextview.visibility = View.INVISIBLE
                })
            }
            val cl = ((Math.floor(sec.toDouble()) * 255) / TIMES_BREATH_LOSE).toInt()
            executor.execute(Runnable { breathTextview.setBackgroundColor(Color.rgb(255, cl, cl)) })

        }
    }


    fun drawGridElements(grid: Grid, canvas: Canvas) {
        val tile = bmpFon.width / 4
        val newTile = (tile / 1.5).toFloat()
        for (y in 0 until grid.height)
            for (x in 0 until grid.width) {
                val screenX = x * newTile
                val screenY = y * newTile
                val offsetX = (grid.space[y][x].background / NUMBER_COLUMNS_IMAGES_FON) * tile
                val offsetY = (grid.space[y][x].background % NUMBER_ROWS_IMAGES_FON) * tile

                canvas.drawBitmap(
                    bmpFon,
                    Rect(offsetX, offsetY, offsetX + tile, offsetY + tile),
                    RectF(screenX, screenY, screenX + newTile, screenY + newTile),
                    paint
                )
            }

        for (y in 0 until grid.height)
            for (x in 0 until grid.width)
                if (grid.space[y][x].block != 0) {
                    val screenX = x * newTile
                    val screenY = y * newTile
                    val offset: Point = getOffset(grid.space[y][x])
                    canvas.drawBitmap(
                        bmpKv[grid.space[y][x].block - 1],
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

    fun drawCurrentFigure(currentFigure: CurrentFigure, canvas: Canvas) {
        val tile = bmpFon.width / 4
        val newTile = (tile / 1.5).toFloat()
        for (cell in currentFigure.cells) {
            val screenX = ((cell.x + currentFigure.position.x) * newTile).toFloat()
            val screenY = ((cell.y + currentFigure.position.y) * newTile).toFloat()
            canvas.drawBitmap(
                bmpKv[cell.view - 1],
                Rect(0, 0, tile, tile),
                RectF(screenX, screenY, screenX + newTile, screenY + newTile),
                paint
            )
        }
    }

    fun drawCharacter(character: Character, canvas: Canvas) {
        val tile = bmpFon.width / 4
        val newTile = (tile / 1.5).toFloat()
        val offset = character.getSprite()
        offset.x *= tile
        offset.y *= tile
        val screenX = (character.position.x * newTile).toFloat()
        val screenY = (character.position.y * newTile).toFloat()
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
}

// Получить смещение по тайлам в зависимости от статуса элемента
fun getOffset(element: Element): Point {
    if (element.getSpaceStatus() == 'R')
        return Point((element.status['R'] ?: 0 - 1), 1)

    if (element.getSpaceStatus() == 'L')
        return Point((element.status['L'] ?: 0 - 1), 2)

    if (element.getSpaceStatus() == 'U')
        return Point((element.status['U'] ?: 0 - 1), 3)

    return Point(0, 0)
}
