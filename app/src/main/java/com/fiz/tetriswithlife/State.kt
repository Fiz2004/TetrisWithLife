package com.fiz.tetriswithlife

import android.content.SharedPreferences
import com.fiz.tetriswithlife.character.CharacterBreath
import com.fiz.tetriswithlife.figure.CurrentFigure
import com.fiz.tetriswithlife.figure.Figure
import com.fiz.tetriswithlife.grid.Grid
import com.fiz.tetriswithlife.grid.Point
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

private const val NUMBER_FRAMES_ELEMENTS = 4

// Время без дыхания для проигрыша
private const val TIMES_BREATH_LOSE = 60

class State(
  width: Int,
  height: Int,
  _settings: SharedPreferences
) {
  val grid = Grid(width, height)
  val character = CharacterBreath(grid)

  var scores = 0
  var record = _settings.getInt("Record", 0)

  var status = "playing"

  var nextFigure: Figure = Figure()
  var currentFigure: CurrentFigure = CurrentFigure(grid, nextFigure)

  private val settings = _settings
  private var pauseTime: Long = System.currentTimeMillis()

  private fun createCurrentFigure() {
    currentFigure = CurrentFigure(grid, nextFigure)
    nextFigure = Figure()
  }

  fun update(deltaTime: Float, controller: Controller): Boolean {
    if (!actionsControl(controller)
      || (!character.isBreath(grid) && (checkLose() || isCrushedBeetle()))
      || status == "new game"
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

  private fun actionsControl(controller: Controller): Boolean {
    val status = currentFigure.moves(controller)
    if (status == "endGame"
      // Фигура достигла препятствия
      || (status == "fall" && isCrushedBeetle())
    )
    // Стакан заполнен игра окончена
      return false

    if (status == "fixation") {
      fixation()
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

  private fun fixation() {
    val tile = currentFigure.getPositionTile()
    for ((index, value) in tile.withIndex())
      grid.space[value.y][value.x].block =
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

  private fun ifRecord() {
    val tempRecord = settings.getInt("Record", 0)
    if (scores > tempRecord) {
      record = scores
      val prefEditor: SharedPreferences.Editor = settings.edit()
      prefEditor.putInt("Record", scores)
      prefEditor.apply()
    }
  }

  private fun changeGridDestroyElement() {
    val offset = Point(character.move.x, character.move.y)
    if (offset.x == -1)
      offset.x = 0

    val tile = Point(
      floor(character.position.x).toInt() + offset.x,
      (character.position.y.roundToInt() + offset.y),
    )
    grid.space[tile.y][tile.x].status[character.getDirectionEat()] =
      getStatusDestroyElement() + 1

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

  private fun checkLose(): Boolean {
    if ((this.grid.isNotFree(character.posTile) && character.eat == 0)
      || TIMES_BREATH_LOSE - ceil(
        (System.currentTimeMillis() - character.timeBreath)
                / 1000.0
      ) <= 0
    )
      return true

    return false
  }

  fun clickPause() {
    if (status == "playing") {
      status = "pause"
      pauseTime = System.currentTimeMillis()
    } else {
      status = "playing"
      character.timeBreath += System.currentTimeMillis() - pauseTime
    }
  }
}
