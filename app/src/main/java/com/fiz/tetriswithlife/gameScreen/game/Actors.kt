package com.fiz.tetriswithlife.gameScreen.game

import android.util.Log
import com.fiz.tetriswithlife.gameScreen.domain.models.Controller
import com.fiz.tetriswithlife.gameScreen.game.character.Character
import com.fiz.tetriswithlife.gameScreen.game.figure.CurrentFigure
import com.fiz.tetriswithlife.gameScreen.game.figure.Figure
import java.io.Serializable
import kotlin.math.floor
import kotlin.math.roundToInt

private const val SCORES_FOR_ROW = 100

class Actors(
    var grid: Grid = Grid(),

    var currentFigure: CurrentFigure = CurrentFigure.create(grid.width),

    var character: Character = Character.create(grid),

    var nextFigure: Figure = Figure(),

    var actorsStatus: ActorsStatus = ActorsStatus.Continue,
) : Serializable {


    private val isCharacterCrushedCurrentFigure
        get() =
            currentFigure.isStatusLastMovedDownFall && isCrushedCharacter()

    private val isEndGame
        get() = isGridFull()
                || character.isCharacterNoBreath
                || isCharacterCrushedCurrentFigure

    fun update(
        deltaTime: Double,
        controller: Controller,
        getScores: () -> Int,
        plusScores: (Int) -> Unit
    ) {
        when (actorsStatus) {
            ActorsStatus.Continue -> {
                currentFigure.update(deltaTime, controller, grid::isCollisionPoint)
                characterUpdate(deltaTime, plusScores)

                actorsStatus = if (isEndGame)
                    ActorsStatus.End()
                else
                    ActorsStatus.Continue

                if (actorsStatus !is ActorsStatus.End && currentFigure.isStatusLastMovedDownFixation) {
                    fixation(nextFigure, getScores(), plusScores)
                    nextFigure = Figure()
                }
            }
            is ActorsStatus.End -> {
                actorsStatus.timeToRestart -= deltaTime
                if (actorsStatus.timeToRestart < 0)
                    actorsStatus = ActorsStatus.NewGame
            }
            is ActorsStatus.NewGame -> {}
        }
    }

    private fun characterUpdate(deltaTime: Double, plusScores: (Int) -> Unit) {
        character.update(deltaTime, grid::isOutside, grid::isNotFree)

        if (character.isEatFinish) {
            val tile = character.position.posTile
            grid.space[tile.y][tile.x].setZero()
            plusScores(50)
            val isPathUp = isPathUp(
                character.position.posTile,
                grid.getFullCopySpace()
            )
            character.setBreath(isPathUp)
            character.eatenFinish()
        }

        if (character.isEating())
            changeGridDestroyElement()
    }

    fun isPathUp(tile: Vector, tempSpace: MutableList<MutableList<Int>>): Boolean {
        if (tile.y == 0)
            return true

        tempSpace[tile.y][tile.x] = 1

        for (shiftPoint in Character.Companion.Direction.values()) {
            val nextElement = tile + shiftPoint.value
            if (isInside(tempSpace, nextElement) && isFree(tempSpace, nextElement)
                && isPathUp(nextElement, tempSpace)
            )
                return true
        }
        return false
    }

    private fun isInside(tempSpace: MutableList<MutableList<Int>>, p: Vector): Boolean {
        return p.y in tempSpace.indices && p.x in tempSpace[p.y].indices
    }

    private fun isFree(tempSpace: MutableList<MutableList<Int>>, p: Vector): Boolean {
        return tempSpace[p.y][p.x] == 0
    }

    private fun changeGridDestroyElement() {
        val offsetX = if (character.move == Character.Companion.Direction.Left)
            0
        else
            Character.Companion.Direction.Right.value.x

        val offset = Vector(offsetX, character.move.value.y)

        val tile = Vector(
            floor(character.position.x).toInt() + offset.x,
            character.position.y.roundToInt() + offset.y,
        )

        grid.space[tile.y][tile.x].changeStatus(character.move, character.position.x % 1)
    }

    private fun isGridFull(): Boolean {
        val isCurrentFigureFixation =
            currentFigure.statusLastMovedDown == CurrentFigure.Companion.StatusMoved.Fixation

        val isCurrentFigureAboveGrid = currentFigure.getPositionTile()
            .any { (it.y - 1) < 0 }

        return isCurrentFigureFixation && isCurrentFigureAboveGrid
    }

    private fun isCrushedCharacter(): Boolean {

        val isCharacterCollisionCurrentFigure = currentFigure.getPositionTile()
            .any { tileFigure ->
                tileFigure == character.positionTile
            }

        val isCharacterNoEatAndCollisionBlockGrid =
            !character.eat && grid.isNotFree(character.positionTile)

        return isCharacterCollisionCurrentFigure || isCharacterNoEatAndCollisionBlockGrid
    }

    fun fixation(nextFigure: Figure, scores: Int, plusScores: (Int) -> Unit) {
        val tile = currentFigure.getPositionTile()
        for ((index, value) in tile.withIndex()) {
            if (value.y < 0)
                Log.e("Er", currentFigure.statusLastMovedDown.toString())
            grid.space[value.y][value.x].fixationCell(currentFigure.figure.cells[index].block)
        }
        val countRowFull = grid.countRowFull
        if (countRowFull != 0)
            grid.removeRows()

        for (i in 1..countRowFull)
            plusScores(i * SCORES_FOR_ROW)

        currentFigure.updateStepMoveAuto(scores)

        character.onDeleteRow()

        val isPathUp = isPathUp(
            character.position.posTile,
            grid.getFullCopySpace()
        )
        character.setBreath(isPathUp)

        currentFigure = CurrentFigure.create(grid.width, nextFigure)
    }

    companion object {
        private const val SecTimeForRestartForEndGame = 1.0

        sealed class ActorsStatus(var timeToRestart: Double = SecTimeForRestartForEndGame) :
            Serializable {
            object Continue : ActorsStatus()
            class End : ActorsStatus(timeToRestart = SecTimeForRestartForEndGame)
            object NewGame : ActorsStatus()
        }
    }

}