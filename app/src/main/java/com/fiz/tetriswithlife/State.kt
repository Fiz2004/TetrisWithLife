package com.fiz.tetriswithlife

import android.content.Context
import android.widget.TextView
import android.content.SharedPreferences


private const val NUMBER_FRAMES_ELEMENTS = 4

// Время без дыхания для проигрыша
private const val TIMES_BREATH_LOSE = 60

class State(
    val width: Int,
    val height: Int,
    _scoresTextView: TextView,
    _settings: SharedPreferences,
    _recordTextView: TextView,
    _infoBreathTextview: TextView,
    _breathTextview: TextView
) {
    val grid = Grid(width, height)
    val character = CharacterBreath(grid)
    val scoresTextView = _scoresTextView
    val recordTextView = _recordTextView
    val settings = _settings

    var scores = 0
    var record = settings!!.getInt("Record", 0)

    var status = "playing"

    var pauseTime: Long = System.currentTimeMillis()

    var nextFigure: Figure = Figure()
    var currentFigure: CurrentFigure = CurrentFigure(grid, nextFigure)

    fun createCurrentFigure() {
        currentFigure = CurrentFigure(grid, nextFigure)
        nextFigure = Figure()
    }

    fun update(deltaTime: Float, controller: Controller): Boolean {
        if (actionsControl(controller) == false
            || (!character.isBreath(grid) && (checkLose() || isCrushedBeetle()))
            || status === "new game"
        ) {
            ifRecord()
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

    fun actionsControl(controller: Controller): Boolean {
        val status = currentFigure.moves(controller)
        if (status === "endGame"
            // Фигура достигла препятствия
            || (status === "fall" && isCrushedBeetle())
        )
        // Стакан заполнен игра окончена
            return false

        if (status === "fixation") {
            fixation()
            createCurrentFigure()
        }

        return true
    }

    fun isCrushedBeetle(): Boolean {
        val tile = character.posTile
        for (elem in currentFigure.getPositionTile())
            if ((elem.x == tile.x && elem.y == tile.y)
                || (grid.isNotFree(tile) && character.eat == 0)
            )
                return true

        return false
    }

    fun fixation() {
        val tile = currentFigure.getPositionTile()
        for (index in tile.indices)
            grid.space[tile[index].y][tile[index].x].block =
                currentFigure.cells[index].view

        val countRowFull = grid.getCountRowFull()
        if (countRowFull != 0)
            grid.deleteRows()

        val scoresForRow = 100
        for (i in 1..countRowFull)
            scores += i * scoresForRow

        currentFigure.fixation(scores)
        ifRecord()

        character.deleteRow = 1
        character.isBreath(grid)
    }

    fun ifRecord() {
        val tempRecord = settings!!.getInt("Record", 0)
        if (scores > tempRecord) {
            record = scores
            val prefEditor: SharedPreferences.Editor = settings.edit()
            prefEditor.putInt("Record", scores)
            prefEditor.apply()
        }
    }

    fun changeGridDestroyElement() {
        val offset = Point(character.move.x, character.move.y)
        if (offset.x == -1)
            offset.x = 0

        val tile = Point(
            Math.floor(character.position.x).toInt() + offset.x,
            (Math.round(character.position.y) + offset.y).toInt(),
        )
        grid.space[tile.y][tile.x].status[character.getDirectionEat()] =
            getStatusDestroyElement() + 1

    }

    fun getStatusDestroyElement(): Int {
        if (character.angle == 0F)
            return Math.floor((character.position.x % 1) * NUMBER_FRAMES_ELEMENTS.toDouble())
                .toInt()
        if (character.angle == 180F)
            return 3 - Math.floor((character.position.x % 1) * NUMBER_FRAMES_ELEMENTS.toDouble())
                .toInt()

        return 0
    }

    fun checkLose(): Boolean {
        if ((this.grid.isNotFree(character.posTile) && character.eat == 0)
            || TIMES_BREATH_LOSE - Math.ceil(
                (System.currentTimeMillis() - character.timeBreath)
                        / 1000.0
            ) <= 0
        )
            return true

        return false
    }

    fun clickPause() {
        if (status === "playing") {
            status = "pause"
            pauseTime = System.currentTimeMillis()
        } else {
            status = "playing"
            character.timeBreath += System.currentTimeMillis() - pauseTime
        }
    }
}
