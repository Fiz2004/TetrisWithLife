package com.fiz.tetriswithlife.game.domain.useCase

import com.fiz.tetriswithlife.game.domain.character.Character
import com.fiz.tetriswithlife.game.domain.character.DIRECTION
import com.fiz.tetriswithlife.game.domain.character.Speed
import com.fiz.tetriswithlife.game.domain.character.StatusCharacter
import com.fiz.tetriswithlife.game.domain.models.Grid
import com.fiz.tetriswithlife.game.domain.models.Coordinate
import com.fiz.tetriswithlife.game.domain.models.Point
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private const val CHARACTER_SPEED_ROTATE = 45

private const val PROBABILITY_EAT = 20

class UpdateCharacterUseCase @Inject constructor() {

    var eat = 0

    operator fun invoke(
        grid: Grid,
        character: Character,
        deltaTime: Double
    ): StatusCharacter {

        if (!character.breath)
            character.timeBreath -= deltaTime
        return fun(): StatusCharacter {
            val tempEat = character.eat
            eat = 0
            val statusUpdate = fun(): StatusCharacter {
                changePosition(character)

                if (isNewFrame(character))
                    return updateNewFrame(grid, character)

                return StatusCharacter.Nothing
            }.invoke()

            if (isNewFrame(character)) {
                if (tempEat == 1)
                    return StatusCharacter.EatFinish

                return StatusCharacter.Nothing
            }

            if (tempEat == 1 && character.isMoveStraight()) {
                eat = 1
                return StatusCharacter.Eat
            }

            return statusUpdate
        }.invoke()
    }

    private fun changePosition(character: Character) {
        if (character.speed.rotate == 0F) {
            character.position += Coordinate(
                character.speed.line * character.directionX.toDouble(),
                character.speed.line * character.directionY.toDouble()
            )
        } else {
            character.angle += character.speed.rotate
            character.angle %= 360
            if (character.angle < 0)
                character.angle += 360
        }
    }

    private fun isNewFrame(character: Character): Boolean {
        return (character.position.x % 1 < (1 / 10000.0)
                || character.position.x % 1 > (1 - (1 / 10000.0))
                ) && (character.position.y % 1 < (1 / 10000.0)
                || character.position.y % 1 > (1 - (1 / 10000.0))
                ) && ((character.angle / CHARACTER_SPEED_ROTATE) % 2 < 0.01
                || (character.angle / CHARACTER_SPEED_ROTATE) % 2 > 1.99)
    }

    private fun updateNewFrame(grid: Grid, character: Character): StatusCharacter {
        character.moves = getDirection(grid, character)

        if (character.move.x == character.moves[0].x && character.move.y == character.moves[0].y) {
            if (character.isMoveStraight())
                character.move = character.moves.removeAt(0)
        } else {
            character.move = character.moves.first()
        }

        character.speed = getSpeedAngle(character)

        return StatusCharacter.Nothing
    }

    private fun getSpeedAngle(character: Character): Speed {
        val tempAngle = atan2(character.move.y.toDouble(), character.move.x.toDouble()) * (180 / Math.PI)
        var sign = 1
        if ((character.angle - tempAngle) > 0 && (character.angle - tempAngle) < 180)
            sign = -1

        if (cos(character.angle * (Math.PI / 180)).roundToInt() == character.move.x
            && sin(character.angle * (Math.PI / 180)).roundToInt() == character.move.y
        )
            return Speed((1 / 1000.0).toFloat(), 0F)

        if (character.angle == tempAngle.toFloat())
            return Speed(0F, 0F)

        return Speed(0F, (sign * CHARACTER_SPEED_ROTATE).toFloat())
    }

    private fun getDirection(grid: Grid, character: Character): MutableList<Point> {
        // Проверяем свободен ли выбранный путь при фиксации фигуры
        if (character.deleteRow == 1
            && character.moves == isCanMove(listOf(character.moves), grid, character)
        )
            character.deleteRow = 0

        if (character.moves.isEmpty() || character.deleteRow == 1)
            return getNewDirection(grid, character)

        return character.moves
    }

    private fun getNewDirection(grid: Grid, character: Character): MutableList<Point> {
        val direction = DIRECTION()
        character.deleteRow = 0
        // Если двигаемся вправо
        if (((character.speed.line == 0F && character.speed.rotate == 0F) && character.move.x == 1) || character.move.x == 1) {
            character.lastDirection = 1
            return isCanMove(
                direction.RIGHT_DOWN + direction.RIGHT + direction.LEFT, grid, character
            ).toMutableList()
        }
        // Если двигаемся влево
        if (((character.speed.line == 0F && character.speed.rotate == 0F) && character.move.x == -1) || character.move.x == -1) {
            character.lastDirection = -1
            return isCanMove(
                listOf(direction.LEFT_DOWN, direction.LEFT, direction.RIGHT).flatten(),
                grid, character
            )
                .toMutableList()
        }

        if (character.lastDirection == -1)
            return isCanMove(
                listOf(direction._0D) + direction.LEFT + direction.RIGHT,
                grid, character
            ).toMutableList()

        return isCanMove(
            listOf(direction._0D) + direction.RIGHT + direction.LEFT,
            grid, character
        )
            .toMutableList()
    }

    fun isCanMove(listDirections: List<List<Point>>, grid: Grid, character: Character): List<Point> {
        for (directions in listDirections)
            if (isCanDirections(directions, grid, (0..100).shuffled().first() < PROBABILITY_EAT, character))
                return directions
        return listOf(Point(0, 0))
    }

    private fun isCanDirections(
        directions: List<Point>,
        grid: Grid,
        isDestroy: Boolean,
        character: Character
    ): Boolean {
        val result = mutableListOf<Point>()
        var addPoint = Point(0, 0)
        for (direction in directions) {
            addPoint = addPoint.plus(direction)
            val point = character.posTile.plus(addPoint)

            if (grid.isOutside(point))
                return false

            result += direction

            if (grid.isNotFree(point)) {
                if (addPoint.y == 0 && isDestroy) {
                    eat = 1
                    return true
                }
                return false
            }
        }

        return true
    }

}