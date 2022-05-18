package com.fiz.tetriswithlife.game.domain

import com.fiz.tetriswithlife.game.domain.character.CharacterBreath
import com.fiz.tetriswithlife.game.domain.figure.CurrentFigure
import com.fiz.tetriswithlife.game.domain.grid.Grid
import com.fiz.tetriswithlife.game.domain.models.Figure
import com.fiz.tetriswithlife.game.domain.models.Point
import java.io.Serializable
import kotlin.math.floor
import kotlin.math.roundToInt

private const val NUMBER_FRAMES_ELEMENTS = 4

class GameState(
    width: Int,
    height: Int,
    startRecord: Int
) : Serializable {
    var grid = Grid(width, height)
    var character = CharacterBreath(grid)
    var scores = 0
    var record = startRecord
    var status = "playing"
    var nextFigure: Figure = Figure()
    var currentFigure: CurrentFigure = CurrentFigure(grid, nextFigure)

    fun new(startRecord: Int) {
        grid = Grid(grid.width, grid.height)
        character = CharacterBreath(grid)
        scores = 0
        record = startRecord
        status = "playing"
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

        if (!actionsControl(controller, updateRecord)
            || (!character.breath && (isLose() || isCrushedBeetle()))
            || status == "new game"
        ) {
            updateRecord()
            return false
        }
        val statusCharacter = character.update(grid)
        if (statusCharacter == "eat") {
            val tile = character.posTile
            grid.space[tile.y][tile.x].setZero()
            scores += 50
        } else if (statusCharacter == "eatDestroy") {
            changeGridDestroyElement()
        }

        return true
    }

    private fun actionsControl(controller: Controller, updateRecord: () -> Unit): Boolean {
        val status = currentFigure.moves(controller)

        if (status == "endGame"
            // Фигура достигла препятствия
            || (status == "fall" && isCrushedBeetle())
        )
        // Стакан заполнен игра окончена
            return false

        if (status == "fixation") {
            fixation(updateRecord)
            createCurrentFigure()
        }

        return true
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
            grid.deleteRows()
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
        status = if (status == "playing") "pause" else "playing"

    }
}
