package com.fiz.tetriswithlife.gameScreen.game

import com.fiz.tetriswithlife.gameScreen.domain.models.Controller
import com.fiz.tetriswithlife.gameScreen.game.character.Character
import com.fiz.tetriswithlife.gameScreen.game.figure.CurrentFigure
import com.fiz.tetriswithlife.gameScreen.game.figure.Figure
import java.io.Serializable
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.random.Random

private const val NUMBER_IMAGES_BACKGROUND = 16
private const val SCORES_FOR_ROW = 100

class Grid private constructor(val space: List<List<Element>>) : Serializable {

    var currentFigure: CurrentFigure = CurrentFigure.create(this.space.first().size)
        private set

    var character: Character = Character.create(this)
        private set

    val countRowFull
        get() = space.fold(0) { acc, row ->
            acc + if (row.all { it.block != 0 })
                1
            else
                0
        }

    private val isCharacterCrushedCurrentFigure =
        currentFigure.isStatusLastMovedDownFall && isCrushedCharacter()

    private val isEndGame = isGridFull()
            || character.isCharacterNoBreath
            || isCharacterCrushedCurrentFigure

    private val statusGame = if (isEndGame)
        Game.Companion.LoopStatusGame.End()
    else
        Game.Companion.LoopStatusGame.Continue

    fun update(
        deltaTime: Double,
        controller: Controller,
        plusScores: (Int) -> Unit
    ): Game.Companion.LoopStatusGame {
        currentFigure.update(deltaTime, controller, ::isCollisionPoint)
        characterUpdate(deltaTime, plusScores)

        return statusGame
    }

    fun isCollisionPoint(point: Vector) = point.y >= space.size
            || (point.x !in space.first().indices)
            || isInside(point) && isNotFree(point)

    fun isInside(point: Vector) =
        point.y in space.indices && point.x in space[point.y].indices

    private fun characterUpdate(deltaTime: Double, plusScores: (Int) -> Unit) {
        character.update(deltaTime, ::isOutside, ::isNotFree)

        if (character.isEatFinish) {
            val tile = character.position.posTile
            space[tile.y][tile.x].setZero()
            plusScores(50)
            val isPathUp = isPathUp(
                character.position.posTile,
                getFullCopySpace()
            )
            character.setBreath(isPathUp)
            character.eatenFinish()
        }

        if (character.isEating())
            changeGridDestroyElement()
    }

    fun isOutside(point: Vector) =
        point.y !in space.indices || point.x !in space[point.y].indices


    fun isFree(point: Vector): Boolean = space[point.y][point.x].block == 0

    private fun isPathUp(tile: Vector, tempSpace: MutableList<MutableList<Int>>): Boolean {
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

    private fun getFullCopySpace(): MutableList<MutableList<Int>> {
        return space.map { row -> row.map { it.block }.toMutableList() }.toMutableList()
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

        space[tile.y][tile.x].changeStatus(character.move, character.position.x % 1)
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
            !character.eat && isNotFree(character.positionTile)

        return isCharacterCollisionCurrentFigure || isCharacterNoEatAndCollisionBlockGrid
    }

    fun fixation(nextFigure: Figure, scores: Int, plusScores: (Int) -> Unit) {
        val tile = currentFigure.getPositionTile()
        for ((index, value) in tile.withIndex())
            space[value.y][value.x].fixationCell(currentFigure.figure.cells[index].block)

        val countRowFull = countRowFull
        if (countRowFull != 0)
            removeRows()

        for (i in 1..countRowFull)
            plusScores(i * SCORES_FOR_ROW)

        currentFigure.updateStepMoveAuto(scores)

        character.onDeleteRow()

        val isPathUp = isPathUp(
            character.position.posTile,
            getFullCopySpace()
        )
        character.setBreath(isPathUp)

        currentFigure = CurrentFigure.create(this.space.first().size, nextFigure)
    }

    fun removeRows() {
        for ((index, value) in space.withIndex())
            if (value.all { it.block != 0 })
                removeAndAddRow(index)
    }

    private fun removeAndAddRow(index: Int) {
        for (i in index downTo 1)
            for (j in space[i].indices)
                space[i][j].setElement(space[i - 1][j])
        space.first().forEach { it.setZero() }
    }

    fun isNotFree(point: Vector) = space[point.y][point.x].block != 0

    companion object {
        fun create(
            width: Int,
            height: Int,
            valueFon: () -> Int = { Random.nextInt(NUMBER_IMAGES_BACKGROUND) }
        ): Grid {
            return Grid(
                space = List(height) {
                    List(width) {
                        Element(valueFon())
                    }
                }
            )
        }
    }
}