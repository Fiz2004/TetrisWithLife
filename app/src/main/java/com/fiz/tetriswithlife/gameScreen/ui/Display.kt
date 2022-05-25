package com.fiz.tetriswithlife.gameScreen.ui

import android.graphics.Canvas
import android.graphics.Paint
import com.fiz.tetriswithlife.gameScreen.domain.repositories.BitmapRepository
import com.fiz.tetriswithlife.gameScreen.ui.models.*
import javax.inject.Inject

class Display @Inject constructor(
    private val bitmapRepository: BitmapRepository
) {
    private val paint: Paint = Paint()

    fun render(gameState: GameState, canvas: Canvas, color: Int) {

        canvas.drawColor(color)

        drawGridElements(gameState.backgroundsUi, gameState.blocksUi, canvas)
        drawCharacter(gameState.characterUi, canvas)
        drawCurrentFigure(gameState.blocksCurrentFigureUi, canvas)
    }

    fun renderInfo(gameState: GameState, nextFigureCanvas: Canvas, color: Int) {
        drawNextFigure(gameState.blocksNextFigureUi, nextFigureCanvas, color)
    }

    private fun drawGridElements(
        backgroundsUi: List<BackgroundUi>,
        blocksUi: List<BlockUi>,
        canvas: Canvas
    ) {

        backgroundsUi.forEach {
            canvas.drawBitmap(bitmapRepository.bmpFon, it.src, it.dst, paint)
        }

        blocksUi.forEach {
            canvas.drawBitmap(bitmapRepository.bmpKv[it.value], it.src, it.dst, paint)
        }

    }

    private fun drawCharacter(characterUi: CharacterUi, canvas: Canvas) {
        canvas.drawBitmap(
            bitmapRepository.bmpCharacter,
            characterUi.src, characterUi.dst,
            paint
        )
    }

    private fun drawCurrentFigure(currentFigureUi: List<CurrentFigureUi>, canvas: Canvas) {
        currentFigureUi.forEach {
            canvas.drawBitmap(bitmapRepository.bmpKv[it.value], it.src, it.dst, paint)
        }
    }

    private fun drawNextFigure(nextFigureUi: List<NextFigureUi>, canvasInfo: Canvas, color: Int) {
        canvasInfo.drawColor(color)

        nextFigureUi.forEach {
            canvasInfo.drawBitmap(bitmapRepository.bmpKv[it.value], it.src, it.dst, paint)
        }
    }

}