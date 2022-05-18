package com.fiz.tetriswithlife.game.domain

import android.graphics.Color
import com.fiz.tetriswithlife.R
import com.fiz.tetriswithlife.game.domain.character.CharacterBreath
import com.fiz.tetriswithlife.game.domain.character.TIMES_BREATH_LOSE
import com.fiz.tetriswithlife.game.domain.figure.CurrentFigure
import com.fiz.tetriswithlife.game.domain.grid.Grid
import com.fiz.tetriswithlife.game.domain.models.Figure
import com.fiz.tetriswithlife.game.domain.models.Point
import java.io.Serializable
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.roundToInt

private const val NUMBER_FRAMES_ELEMENTS = 4

data class GameState(
    val width: Int,
    val height: Int,
    val startRecord: Int,
    var grid: Grid = Grid(width, height),
    var character: CharacterBreath = CharacterBreath(grid),
    var scores: Int = 0,
    var record: Int = startRecord,
    var status: StatusGame = StatusGame.Playing,
    var nextFigure: Figure = Figure(),
    var currentFigure: CurrentFigure = CurrentFigure(grid, nextFigure),
    val changed: Boolean = false
) : Serializable {
    val scoresFormat
        get() = scores.toString().padStart(6, '0')

    val recordFormat
        get() = record.toString().padStart(6, '0')

    val textForButtonPause
        get() = if (status == StatusGame.Pause)
            R.string.resume_game_button
        else
            R.string.pause_game_button

    val infoBreathTextViewNotVisibility
        get() = character.breath

    private val sec = if (character.breath)
        TIMES_BREATH_LOSE
    else
        max(character.timeBreath, 0.0)

    val textForBreathTextView: String
        get() {
            return sec.toInt().toString()
        }

    private val color = ((floor(
        sec
    ) * 255) / TIMES_BREATH_LOSE).toInt()

    val colorForBreathTextView: Int
        get() {
            return Color.rgb(
                255, color, color
            )
        }

    fun new(startRecord: Int) {
        grid = Grid(grid.width, grid.height)
        character = CharacterBreath(grid)
        scores = 0
        record = startRecord
        status = StatusGame.Playing
        nextFigure = Figure()
        currentFigure = CurrentFigure(grid, nextFigure)
    }

    private fun createCurrentFigure() {
        currentFigure = CurrentFigure(grid, nextFigure)
        nextFigure = Figure()
    }

    fun update(controller: Controller, deltaTime: Double, updateRecord: () -> Unit): Boolean {
        if (!character.breath)
            character.timeBreath -= deltaTime

        if (controller.isCannotTimeLast(deltaTime))
            return true

        val status = currentFigure.moves(controller)

        if (isEndGame(status, updateRecord)) {
            updateRecord()
            return false
        }

        if (!character.breath && (isLose() || isCrushedBeetle())) {
            updateRecord()
            return false
        }

        if (this.status == StatusGame.NewGame) {
            updateRecord()
            return false
        }

        val statusCharacter = character.update(grid)
        if (statusCharacter == "eat") {
            val tile = character.posTile
            grid.space[tile.y][tile.x].setZero()
            scores += 50
            updateRecord()
        } else if (statusCharacter == "eatDestroy") {
            changeGridDestroyElement()
        }

        return true
    }

    private fun isEndGame(status: CurrentFigure.Companion.StatusMoved, updateRecord: () -> Unit): Boolean {
        if (status == CurrentFigure.Companion.StatusMoved.EndGame
            // Фигура достигла препятствия
            || (status == CurrentFigure.Companion.StatusMoved.Fall && isCrushedBeetle())
        )
        // Стакан заполнен игра окончена
            return true

        if (status == CurrentFigure.Companion.StatusMoved.Fixation) {
            fixation(updateRecord)
            createCurrentFigure()
        }

        return false
    }

    private fun isCrushedBeetle(): Boolean {
        val tile = character.posTile
        for (elem in currentFigure.getPositionTile())
            if (elem == tile
                || (grid.isNotFree(tile) && character.eat == 0)
            )
                return true

        return false
    }

    private fun fixation(updateRecord: () -> Unit) {
        val tile = currentFigure.getPositionTile()
        for ((index, value) in tile.withIndex())
            grid.space[value.y][value.x].block =
                currentFigure.figure.cells[index].view
        val countRowFull = grid.getCountRowFull()
        if (countRowFull != 0)
            grid = grid.deleteRows()
        val scoresForRow = 100
        for (i in 1..countRowFull)
            scores += i * scoresForRow

        currentFigure.fixation(scores)
        updateRecord()

        character.deleteRow = 1
        character.isBreath(grid)
    }

    private fun changeGridDestroyElement() {
        val newX = if (character.move.x == -1)
            0
        else
            character.move.x

        val offset = Point(newX, character.move.y)

        val tile = Point(
            floor(character.position.x).toInt() + offset.x,
            (character.position.y.roundToInt() + offset.y),
        )

        grid.space[tile.y][tile.x].status[character.getDirectionEat()] =
            getStatusDestroyElement() + 1

        character.isBreath(grid)
    }

    private fun getStatusDestroyElement(): Int {
        if (character.angle == 0F)
            return floor((character.position.x % 1) * NUMBER_FRAMES_ELEMENTS.toDouble())
                .toInt()
        if (character.angle == 180F)
            return 3 - floor((character.position.x % 1) * NUMBER_FRAMES_ELEMENTS.toDouble())
                .toInt()

        return 0
    }

    private fun isLose(): Boolean {
        if (this.grid.isNotFree(character.posTile) && character.eat == 0)
            return true

        if (character.timeBreath <= 0)
            return true

        return false
    }

    fun clickPause() {
        status = if (status == StatusGame.Playing) StatusGame.Pause else StatusGame.Playing
    }

    companion object{
        enum class StatusGame{
            Playing,Pause,NewGame
        }
    }
}


