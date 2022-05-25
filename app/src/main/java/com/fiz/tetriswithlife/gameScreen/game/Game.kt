package com.fiz.tetriswithlife.gameScreen.game

import com.fiz.tetriswithlife.gameScreen.domain.models.Controller
import com.fiz.tetriswithlife.gameScreen.domain.repositories.RecordRepository
import com.fiz.tetriswithlife.gameScreen.game.character.Character
import com.fiz.tetriswithlife.gameScreen.game.character.PresetDirection
import com.fiz.tetriswithlife.gameScreen.game.figure.CurrentFigure
import com.fiz.tetriswithlife.gameScreen.game.figure.Figure
import com.fiz.tetriswithlife.gameScreen.ui.SecTimeForRestartForEndGame
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt

const val WIDTH_GRID: Int = 13
const val HEIGHT_GRID: Int = 25

const val PROBABILITY_EAT_PERCENT = 20

private const val NUMBER_FRAMES_ELEMENTS = 4
private const val mSEC_FROM_FPS_60 = ((1.0 / 60.0) * 1000.0).toLong()
private const val SEC_TIME_OUT_INPUT = 0.08

@Singleton
class Game @Inject constructor(
    var width: Int = WIDTH_GRID,
    var height: Int = HEIGHT_GRID,
    private val recordRepository: RecordRepository
) {
    lateinit var grid: Grid
    lateinit var character: Character
    lateinit var nextFigure: Figure
    lateinit var currentFigure: CurrentFigure
    var status: StatusGame = StatusGame.Playing
    var scores: Int = 0
    var lastTime: Long = System.currentTimeMillis()
    var timeToRestart: Double = SecTimeForRestartForEndGame
    var timeLastUpdateController: Double = 0.0
    var isDeleteRow: Boolean = false

    init {
        newGame()
    }

    fun update(controller: Controller) {
        val deltaTime = getDeltaTime()
        if (deltaTime == 0.0) return

        gameTick(deltaTime, controller)
    }

    private fun getDeltaTime(): Double {
        val now = System.currentTimeMillis()
        val deltaTime = min(now - lastTime, mSEC_FROM_FPS_60) / 1000.0
        lastTime = now
        return deltaTime
    }

    private fun gameTick(
        deltaTime: Double,
        controller: Controller
    ) {
        if (status == StatusGame.Pause) return

        if (timeToRestart == SecTimeForRestartForEndGame)
            actorsUpdate(deltaTime, controller)

        if (status == StatusGame.End)
            timeToRestart -= deltaTime

        if (timeToRestart < 0)
            newGame()
    }

    private fun actorsUpdate(
        deltaTime: Double,
        controller: Controller
    ) {
        currentFigureUpdate(deltaTime, controller)

        characterUpdate(deltaTime)

        status = getStatusGame()

        if (status == StatusGame.Playing && currentFigure.isStatusLastMovedDownFixation) {
            fixation()
            createCurrentFigure()
        }
    }

    private fun currentFigureUpdate(
        deltaTime: Double,
        controller: Controller
    ) {
        if (isCanTimeLast(deltaTime)) {
            currentFigureMoveLeft(controller)

            currentFigureMoveRight(controller)

            currentFigureRotate(controller)
        }

        currentFigureMoveDown(controller, deltaTime)
    }

    private fun isCanTimeLast(deltaTime: Double): Boolean {
        if (timeLastUpdateController == 0.0) {
            timeLastUpdateController = SEC_TIME_OUT_INPUT
            return true
        }

        timeLastUpdateController -= deltaTime
        if (timeLastUpdateController < 0.0)
            timeLastUpdateController = 0.0
        return false
    }

    private fun currentFigureMoveLeft(controller: Controller) {
        if (controller.left) {
            val newPosition = currentFigure.positionIfMoveLeft
            if (!isCollision(newPosition))
                currentFigure.setPosition(newPosition)
        }
    }

    private fun currentFigureMoveRight(controller: Controller) {
        if (controller.right) {
            val newPosition = currentFigure.positionIfMoveRight
            if (!isCollision(newPosition))
                currentFigure.setPosition(newPosition)
        }
    }

    private fun currentFigureRotate(controller: Controller) {
        if (controller.up) {
            val oldFigure = currentFigure.figure
            currentFigure.setFigure(currentFigure.getFigureRotate())
            if (isCollision(currentFigure.position))
                currentFigure.setFigure(oldFigure)
        }
    }

    private fun currentFigureMoveDown(controller: Controller, deltaTime: Double) {
        val step = currentFigure.getStepMoveY(controller.down) * deltaTime
        moveDown(step)
    }

    private fun moveDown(stepY: Double) {
        val yStart = currentFigure.tileY
        val yEnd = currentFigure.getTileYIfMoveDownByStep(stepY)
        val yMax = getYByCollisionIfMoveDown(yStart, yEnd)

        if (yMax == yEnd) {

            val addPositionY = if (stepY < 1)
                stepY
            else
                (yMax - yStart).toDouble()

            currentFigure.fall(addPositionY)

            return
        }

        currentFigure.fixation(yMax)

    }

    private fun getYByCollisionIfMoveDown(yStart: Int, yEnd: Int): Int {
        (yStart..yEnd).forEach { y ->
            val coordinate = Coordinate(currentFigure.position.x, y.toDouble())
            if (isCollision(coordinate))
                return y - 1
        }

        return yEnd
    }

    fun isCollision(coordinate: Coordinate): Boolean {
        return currentFigure.getPositionTile(coordinate)
            .any { point ->
                grid.isCollision(point)
            }
    }

    private fun characterUpdate(deltaTime: Double) {
        character.breath.updateBreath(deltaTime)
        character.move(deltaTime)

        val isEatenBefore = character.eat
        if (character.isNewFrame()) {

            character.newFrame(getDirection())

            if (isEatenBefore && character.speed.isMove()) {
                val tile = character.position.posTile
                grid.space[tile.y][tile.x].setZero()
                plusScores(50)
                val isPathUp = isPathUp(
                    character.position.posTile,
                    getFullCopySpace()
                )
                character.setBreath(isPathUp)
            }

            character.setSpeed()
        }

        if (character.isEating())
            changeGridDestroyElement()
    }


    private fun getDirection(): List<Character.Companion.Direction> {
        // Проверяем свободен ли выбранный путь при фиксации фигуры
        if (isDeleteRow
            && character.moves == isCanMove(listOf(character.moves))
        )
            isDeleteRow = false

        return if (character.moves.isEmpty() || isDeleteRow)
            getNewDirection()
        else
            return character.moves
    }

    private fun getNewDirection(): List<Character.Companion.Direction> {

        val presetDirection = PresetDirection()
        isDeleteRow = false
        // Если двигаемся вправо

        val listDirections = when {
            character.speed.isStop() || character.move == Character.Companion.Direction.Right -> {
                character.setLastDirection(Character.Companion.Direction.Right)
                listOf(presetDirection.RIGHT_DOWN + presetDirection.RIGHT + presetDirection.LEFT).flatten()

            }
            // Если двигаемся влево
            character.speed.isStop() || character.move == Character.Companion.Direction.Left -> {
                character.setLastDirection(Character.Companion.Direction.Left)
                listOf(presetDirection.LEFT_DOWN + presetDirection.LEFT + presetDirection.RIGHT).flatten()
            }

            character.lastDirection == Character.Companion.Direction.Left ->
                listOf(presetDirection._0D) + presetDirection.LEFT + presetDirection.RIGHT


            else -> listOf(presetDirection._0D) + presetDirection.RIGHT + presetDirection.LEFT
        }

        return isCanMove(listDirections)
    }

    private fun isCanMove(listDirections: List<List<Character.Companion.Direction>>): List<Character.Companion.Direction> {
        for (directions in listDirections)
            if (isCanDirectionsAndSetCharacterEat(directions))
                return directions
        return listOf(Character.Companion.Direction.Stop)
    }

    fun isCanDirectionsAndSetCharacterEat(
        directions: List<Character.Companion.Direction>,
        isDestroy: Boolean = (0..100).shuffled().first() < PROBABILITY_EAT_PERCENT,
    ): Boolean {
        var result = Vector(0, 0)
        var currentVector = Vector(0, 0)

        directions.forEach { direction ->
            currentVector += direction
            val checkingPosition = character.position.posTile + currentVector

            if (grid.isOutside(checkingPosition))
                return false

            result += direction

            if (grid.isNotFree(checkingPosition)) {
                val isCanEatHorizontally = currentVector.y == 0 && isDestroy
                if (isCanEatHorizontally) {
                    character.setEat(true)
                    return true
                }
                return false
            }
        }
        character.setEat(false)
        return true
    }

    private fun plusScores(score: Int) {
        scores += score
        if (scores > recordRepository.loadRecord())
            recordRepository.saveRecord(scores)
    }

    private fun changeGridDestroyElement() {
        val offsetX = if (character.move == Character.Companion.Direction.Left)
            0
        else
            Character.Companion.Direction.Left.value.x

        val offset = Vector(offsetX, character.move.value.y)

        val tile = Vector(
            floor(character.position.x).toInt() + offset.x,
            (character.position.y.roundToInt() + offset.y),
        )

        grid.space[tile.y][tile.x].changeStatus(
            character.move,
            getStatusDestroyElement() + 1
        )

        val isPathUp = isPathUp(
            character.position.posTile,
            getFullCopySpace()
        )
        character.setBreath(isPathUp)
    }

    private fun getStatusGame(): StatusGame {
        if (isEndGame()) {
            plusScores(0)
            return StatusGame.End
        }

        return StatusGame.Playing
    }

    private fun isEndGame(): Boolean {
        return isGridFull()
                || isCharacterNoBreath()
                || isCharacterCrushedCurrentFigure()
    }

    private fun isGridFull(): Boolean {
        val isCurrentFigureFixation =
            currentFigure.statusLastMovedDown == CurrentFigure.Companion.StatusMoved.Fixation

        val isCurrentFigureAboveGrid = currentFigure.getPositionTile()
            .any { (it.y - 1) < 0 }

        return isCurrentFigureFixation && isCurrentFigureAboveGrid
    }

    private fun isCharacterNoBreath(): Boolean {
        return character.breath.secondsSupplyForBreath <= 0
    }

    private fun isCharacterCrushedCurrentFigure(): Boolean {
        return currentFigure.isStatusLastMovedDownFall && isCrushedCharacter()
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

    private fun getStatusDestroyElement(): Int {
        if (character.angle.isRight())
            return floor((character.position.x % 1) * NUMBER_FRAMES_ELEMENTS.toDouble())
                .toInt()
        if (character.angle.isLeft())
            return 3 - floor((character.position.x % 1) * NUMBER_FRAMES_ELEMENTS.toDouble())
                .toInt()

        return 0
    }

    private fun createCurrentFigure() {
        currentFigure = CurrentFigure.create(grid, nextFigure)
        nextFigure = Figure()
    }

    private fun fixation() {
        val tile = currentFigure.getPositionTile()
        for ((index, value) in tile.withIndex())
            grid.space[value.y][value.x].apply {
                block = currentFigure.figure.cells[index].view
                status = Element.Companion.StatusElement.Whole
            }
        val countRowFull = grid.countRowFull
        if (countRowFull != 0)
            grid.deleteRows()
        val scoresForRow = 100
        for (i in 1..countRowFull) {
            plusScores(i * scoresForRow)
        }

        currentFigure.updateStepMoveAuto(scores)

        isDeleteRow = true
        val isPathUp = isPathUp(
            character.position.posTile,
            getFullCopySpace()
        )
        character.setBreath(isPathUp)
    }

    fun getFullCopySpace(): MutableList<MutableList<Int>> {
        return grid.space.map { it.map { it.block }.toMutableList() }.toMutableList()
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

    fun clickPause() {
        status = if (status == StatusGame.Playing)
            StatusGame.Pause
        else
            StatusGame.Playing
    }

    fun clickNewGame() {
        newGame()
    }

    private fun newGame() {
        grid = Grid.create(width, height)
        character = Character.create(grid)
        nextFigure = Figure()
        currentFigure = CurrentFigure.create(grid)
        scores = 0
        timeToRestart = SecTimeForRestartForEndGame
    }

    companion object {
        enum class StatusGame : Serializable {
            Playing, Pause, End
        }
    }
}
