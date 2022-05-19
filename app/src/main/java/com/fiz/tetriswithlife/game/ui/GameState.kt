package com.fiz.tetriswithlife.game.ui

import com.fiz.tetriswithlife.game.domain.character.CharacterBreath
import com.fiz.tetriswithlife.game.domain.figure.CurrentFigure
import com.fiz.tetriswithlife.game.domain.grid.Grid
import com.fiz.tetriswithlife.game.domain.models.Figure
import com.fiz.tetriswithlife.game.domain.models.Point
import java.io.Serializable
import kotlin.math.floor
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
    var status: StatusCurrentGame = StatusCurrentGame.Playing,
    var nextFigure: Figure = Figure(),
    var currentFigure: CurrentFigure = CurrentFigure(grid, nextFigure),
    val changed: Boolean = false
) : Serializable {

    fun isStatusPause(): Boolean {
        return status == StatusCurrentGame.Pause
    }

    fun isStatusNewGame(): Boolean {
        return status == StatusCurrentGame.NewGame
    }

    fun checkRecord(updateRecord: (Int) -> Unit) {
        if (scores > record) {
            record = scores
            updateRecord(scores)
        }
    }

    fun isEndGame(
        updateRecord: (Int) -> Unit
    ): Boolean {

        if ((currentFigure.statusLastMovedDown == CurrentFigure.Companion.StatusMoved.Fixation &&
                    (currentFigure.getPositionTile()
                        .any { (it.y - 1) < 0 }
                            ))// Фигура достигла препятствия
            || (currentFigure.statusLastMovedDown == CurrentFigure.Companion.StatusMoved.Fall && isCrushedBeetle())
        )
        // Стакан заполнен игра окончена
            return true


        if (currentFigure.statusLastMovedDown == CurrentFigure.Companion.StatusMoved.Fixation) {
            fixation(updateRecord)
            createCurrentFigure()
        }

        return false
    }

    private fun createCurrentFigure() {
        currentFigure = CurrentFigure(grid, nextFigure)
        nextFigure = Figure()
    }

    fun isCrushedBeetle(): Boolean {
        val tile = character.posTile
        for (elem in currentFigure.getPositionTile())
            if (elem == tile
                || (grid.isNotFree(tile) && character.eat == 0)
            )
                return true

        return false
    }

    private fun fixation(updateRecord: (Int) -> Unit) {
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
        checkRecord(updateRecord)

        character.deleteRow = 1
        character.isBreath(grid)
    }

    fun changeGridDestroyElement() {
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

    fun isLose(): Boolean {
        if (this.grid.isNotFree(character.posTile) && character.eat == 0)
            return true

        if (character.timeBreath <= 0)
            return true

        return false
    }

    fun clickPause() {
        status = if (status == StatusCurrentGame.Playing)
            StatusCurrentGame.Pause
        else
            StatusCurrentGame.Playing
    }

    companion object {
        enum class StatusCurrentGame {
            Playing, Pause, NewGame
        }

        enum class StatusUpdateGame {
            Continue, End
        }
    }
}

