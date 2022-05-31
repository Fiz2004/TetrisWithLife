package com.fiz.tetriswithlife.gameScreen.game

import com.fiz.tetriswithlife.gameScreen.domain.models.Controller
import com.fiz.tetriswithlife.gameScreen.game.character.Character
import com.fiz.tetriswithlife.gameScreen.game.character.Location
import com.fiz.tetriswithlife.gameScreen.game.figure.CurrentFigure
import com.fiz.tetriswithlife.gameScreen.game.figure.Figure
import com.fiz.tetriswithlife.gameScreen.ui.GameViewModel
import com.fiz.tetriswithlife.gameScreen.ui.widthGrid
import java.io.Serializable
import kotlin.math.floor
import kotlin.math.roundToInt

private const val NUMBER_FRAMES_ELEMENTS = 4

data class Game(
    val grid: Grid,
    var character: Character = Character(
        Location(
            Coordinate(
                (0 until grid.width).shuffled().first().toDouble(),
                (grid.height - 1).toDouble()
            )
        )
    ),
    var nextFigure: Figure = Figure(),
    var currentFigure: CurrentFigure = run {
        val figure = Figure()
        CurrentFigure(
            figure,
            Coordinate(
                (0 until (widthGrid - figure.getMaxX())).shuffled().first().toDouble(),
                (0 - figure.getMaxY()).toDouble()
            )
        )
    },
    var scores: Int = 0
) : Serializable {

    fun updateActors(
        deltaTime: Double,
        controller: Controller,
        plusScores: (Int) -> Unit
    ): GameViewModel.Companion.StatusUpdateGame {

        currentFigureUpdate(deltaTime, controller)

        characterUpdate(deltaTime, plusScores)

        return getStatusGame(plusScores)

    }

    private fun currentFigureUpdate(
        deltaTime: Double,
        controller: Controller
    ) {
        if (controller.isCanTimeLast(deltaTime)) {
            currentFigureMoveLeft(controller)

            currentFigureMoveRight(controller)

            currentFigureRotate(controller)
        }

        currentFigureMoveDown(controller, deltaTime)
    }

    private fun currentFigureMoveLeft(controller: Controller) {
        if (controller.left) {
            val newPosition = currentFigure.getPositionMoveLeft()
            if (isNotCollision(newPosition))
                currentFigure.position = newPosition
        }
    }

    private fun currentFigureMoveRight(controller: Controller) {
        if (controller.right) {
            val newPosition = currentFigure.getPositionMoveRight()
            if (isNotCollision(newPosition))
                currentFigure.position = newPosition
        }
    }

    private fun currentFigureRotate(controller: Controller) {
        if (controller.up) {
            val oldFigure = currentFigure.figure
            currentFigure.figure = currentFigure.getFigureRotate()
            if (isCollision(currentFigure.position))
                currentFigure = currentFigure.copy(figure = oldFigure)
        }
    }

    private fun currentFigureMoveDown(controller: Controller, deltaTime: Double) {
        val step = currentFigure.getStepMoveY(controller.down) * deltaTime
        moveDown(step)
    }

    private fun moveDown(stepY: Double) {
        val yStart = currentFigure.getTileY()
        val yEnd = currentFigure.getTileYIfMoveDownByStep(stepY)
        val yMax = getYByCollisionIfMoveDown(yStart, yEnd)

        if (yMax == yEnd) {

            val addPositionY: Double = if (stepY < 1)
                stepY
            else
                (yMax - yStart).toDouble()

            currentFigure.fall(addPositionY)

            return
        }

        currentFigure.fixation(yMax)

    }

    private fun getYByCollisionIfMoveDown(yStart: Int, yEnd: Int): Int {
        for (y in yStart..yEnd) {
            val coordinate = Coordinate(currentFigure.position.x, y.toDouble())
            if (isCollision(coordinate))
                return y - 1
        }

        return yEnd
    }

    private fun characterUpdate(
        deltaTime: Double,
        plusScores: (Int) -> Unit
    ) {

        character.updateBreath(deltaTime)

        character.move(deltaTime)

        val isEatenBefore = character.eat
        if (character.isNewFrame()) {
            character.updateByNewFrame(this)

            if (isEatenBefore) {
                val tile = character.location.position.posTile
                grid.space[tile.y][tile.x].setZero()
                plusScores(50)
                val isPathUp = isPathUp(
                    character.location.position.posTile,
                    getFullCopySpace()
                )
                character.setBreath(isPathUp)
            }
        }

        if (character.isEating())
            changeGridDestroyElement()
    }

    private fun getStatusGame(plusScores: (Int) -> Unit): GameViewModel.Companion.StatusUpdateGame {
        if (isEndGame(plusScores)) {
            plusScores(0)
            return GameViewModel.Companion.StatusUpdateGame.End
        }

        return GameViewModel.Companion.StatusUpdateGame.Continue
    }

    fun isCollision(coordinate: Coordinate): Boolean {
        if (currentFigure.getPositionTile(coordinate).any { point ->
                (point.x !in 0 until grid.width)
                        || point.y > grid.height - 1
            })
            return true

        if (currentFigure.getPositionTile(coordinate).any { point ->
                isInside(point) && grid.space[point.y][point.x].block != 0
            }
        )
            return true

        return false
    }

    private fun isNotCollision(coordinate: Coordinate): Boolean {
        return !isCollision(coordinate)
    }

    private fun changeGridDestroyElement() {
        val offsetX = if (character.movement.move.x == -1)
            0
        else
            character.movement.move.x

        val offset = Vector(offsetX, character.movement.move.y)

        val tile = Vector(
            floor(character.location.position.x).toInt() + offset.x,
            (character.location.position.y.roundToInt() + offset.y),
        )

        grid.space[tile.y][tile.x].status[character.getDirectionEat()] =
            getStatusDestroyElement() + 1

        val isPathUp = isPathUp(
            character.location.position.posTile,
            getFullCopySpace()
        )
        character.setBreath(isPathUp)
    }

    private fun isEndGame(
        plusScores: (Int) -> Unit
    ): Boolean {
        if (isEndGameFigure(plusScores))
            return true

        if (!character.breath.breath && (isLose() || isCrushedBeetle()))
            return true

        return false
    }

    private fun isLose(): Boolean {
        if (isNotFree(character.location.position.posTile) && !character.eat)
            return true

        if (character.breath.secondsSupplyForBreath <= 0)
            return true

        return false
    }

    private fun isEndGameFigure(
        plusScores: (Int) -> Unit
    ): Boolean {

        if ((currentFigure.isStatusLastMovedDownFixation() &&
                    (currentFigure.getPositionTile()
                        .any { (it.y - 1) < 0 }
                            ))// Фигура достигла препятствия
            || (currentFigure.isStatusLastMovedDownFall() && isCrushedBeetle())
        )
        // Стакан заполнен игра окончена
            return true


        if (currentFigure.isStatusLastMovedDownFixation()) {
            fixation(plusScores)
            createCurrentFigure()
        }

        return false
    }

    private fun getStatusDestroyElement(): Int {
        if (character.location.angle.isRight())
            return floor((character.location.position.x % 1) * NUMBER_FRAMES_ELEMENTS.toDouble())
                .toInt()
        if (character.location.angle.isLeft())
            return 3 - floor((character.location.position.x % 1) * NUMBER_FRAMES_ELEMENTS.toDouble())
                .toInt()

        return 0
    }

    private fun createCurrentFigure() {
        currentFigure = CurrentFigure(
            nextFigure,
            Coordinate(
                (0 until (grid.width - nextFigure.getMaxX())).shuffled().first()
                    .toDouble(),
                (0 - nextFigure.getMaxY()).toDouble()
            )
        )
        nextFigure = Figure()
    }

    private fun isCrushedBeetle(): Boolean {
        val tile = character.location.position.posTile

        for (elem in currentFigure.getPositionTile())
            if (elem == tile
                || (isNotFree(tile) && !character.eat)
            )
                return true

        return false
    }

    private fun fixation(
        plusScores: (Int) -> Unit
    ) {
        val tile = currentFigure.getPositionTile()
        for ((index, value) in tile.withIndex())
            grid.space[value.y][value.x].block =
                currentFigure.figure.cells[index].view
        val countRowFull = getCountRowFull()
        if (countRowFull != 0)
            deleteRows()
        val scoresForRow = 100
        for (i in 1..countRowFull) {
            scores += i * scoresForRow
            plusScores(scores)
        }

        currentFigure.updateStepMoveAuto(scores)

        character.movement.deleteRow = 1
        val isPathUp = isPathUp(
            character.location.position.posTile,
            getFullCopySpace()
        )
        character.setBreath(isPathUp)
    }

    fun getFullCopySpace(): MutableList<MutableList<Int>> {
        val result: MutableList<MutableList<Int>> = mutableListOf()
        grid.space.forEachIndexed { indexY, elements ->
            result.add(mutableListOf())
            elements.forEach { element ->
                result[indexY].add(element.block)
            }
        }
        return result
    }

    private fun isInside(tempSpace: MutableList<MutableList<Int>>, p: Vector): Boolean {
        return p.y in tempSpace.indices && p.x in tempSpace[p.y].indices
    }

    private fun isFree(tempSpace: MutableList<MutableList<Int>>, p: Vector): Boolean {
        return tempSpace[p.y][p.x] == 0
    }

    fun isPathUp(tile: Vector, tempSpace: MutableList<MutableList<Int>>): Boolean {
        if (tile.y == 0)
            return true

        tempSpace[tile.y][tile.x] = 1

        for (shiftPoint in listOf(Vector(0, -1), Vector(1, 0), Vector(-1, 0), Vector(0, 1))) {
            val nextElement = tile + shiftPoint
            if (isInside(tempSpace, nextElement) && isFree(tempSpace, nextElement)
                && isPathUp(nextElement, tempSpace)
            )
                return true
        }
        return false
    }

    fun isInside(p: Vector): Boolean {
        return p.x in 0 until grid.width && p.y in 0 until grid.height
    }

    fun isOutside(p: Vector): Boolean {
        return p.x !in 0 until grid.width || p.y !in 0 until grid.height
    }

    fun isFree(p: Vector): Boolean {
        return this.grid.space[p.y][p.x].block == 0
    }

    fun isNotFree(p: Vector): Boolean {
        return this.grid.space[p.y][p.x].block != 0
    }

    fun getCountRowFull(): Int {
        var result = 0
        for (row in grid.space)
            if (row.all { it.block != 0 })
                result += 1

        return result
    }

    fun deleteRows() {
        for ((index, value) in grid.space.withIndex())
            if (value.all { it.block != 0 }) {
                for (i in index downTo 1)
                    for (j in 0 until grid.width)
                        grid.space[i][j].setElement(grid.space[i - 1][j])
                grid.space[0].forEach { it.setZero() }
            }
    }

    fun newGame() {
        grid.space = List(grid.height) {
            List(grid.width) {
                Element(grid.valueFon())
            }
        }
        character = Character(
            Location(
                Coordinate(
                    (0 until grid.width).shuffled().first().toDouble(),
                    (grid.height - 1).toDouble()
                )
            )
        )
        nextFigure = Figure()
        currentFigure = run {
            val figure = Figure()
            CurrentFigure(
                figure,
                Coordinate(
                    (0 until (widthGrid - figure.getMaxX())).shuffled().first().toDouble(),
                    (0 - figure.getMaxY()).toDouble()
                )
            )
        }
    }

}
