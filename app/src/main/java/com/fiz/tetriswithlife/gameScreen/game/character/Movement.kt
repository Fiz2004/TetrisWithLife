package com.fiz.tetriswithlife.gameScreen.game.character

import com.fiz.tetriswithlife.gameScreen.game.Game
import com.fiz.tetriswithlife.gameScreen.game.Vector
import java.io.Serializable

private const val BASE_SPEED_ROTATE_FOR_SECOND = 45F

private const val PROBABILITY_EAT_PERCENT = 20

private const val BASE_SPEED_FOR_SECOND = 1F / 1000

data class Movement(
    var speed: Speed = Speed(0F, 0F),

    var move: Vector = Vector(0, 0),

    var deleteRow: Int = 0,
    var lastDirection: Int = 1,

    private var moves: MutableList<Vector> = mutableListOf()
) : Serializable {


    fun getDirectionEat(): Char {
        return move.getDirectionEat()
    }

    fun isNotRotated(): Boolean {
        return speed.isNotRotated()
    }

    fun updateByNewFrame(
        posTile: Vector,
        game: Game,
        angle: Float,
        isMoveStraight: Boolean,
        eating: () -> Unit
    ) {
        moves = getDirection(posTile, game, eating).toMutableList()

        move = getMoveFromMoves(isMoveStraight)

        speed = getSpeed(angle, move)
    }

    private fun getDirection(posTile: Vector, game: Game, eating: () -> Unit): List<Vector> {
        // Проверяем свободен ли выбранный путь при фиксации фигуры
        if (deleteRow == 1
            && moves == isCanMove(
                posTile,
                listOf(moves),
                game,
                eating
            )
        )
            deleteRow = 0

        if (moves.isEmpty() || deleteRow == 1)
            return getNewDirection(posTile, game, eating)

        return moves
    }

    private fun getMoveFromMoves(isMoveStraight: Boolean): Vector {
        return if (move.x == moves.first().x && move.y == moves.first().y) {
            if (isMoveStraight)
                moves.removeFirst()
            else
                move
        } else {
            moves.first()
        }
    }

    private fun getSpeed(currentAngle: Float, needVector: Vector): Speed {
        val tempAngle = needVector.getAngleInDegrees()

        var signAtClockwise = 1
        if ((currentAngle - tempAngle) in (0F..180F))
            signAtClockwise = -1

        if (needVector.equalsWith(currentAngle))
            return Speed(BASE_SPEED_FOR_SECOND, 0F)

        if (currentAngle == tempAngle)
            return Speed(0F, 0F)

        return Speed(0F, signAtClockwise * BASE_SPEED_ROTATE_FOR_SECOND)
    }

    private fun getNewDirection(posTile: Vector, game: Game, eating: () -> Unit): List<Vector> {

        val presetDirection = PresetDirection()
        deleteRow = 0
        // Если двигаемся вправо
        if (((speed.line == 0F && speed.rotate == 0F) && move.x == 1) || move.x == 1) {
            lastDirection = 1
            return isCanMove(
                posTile,
                listOf(presetDirection.RIGHT_DOWN + presetDirection.RIGHT + presetDirection.LEFT).flatten(),
                game,
                eating
            )
        }
        // Если двигаемся влево
        if (((speed.line == 0F && speed.rotate == 0F) && move.x == -1) || move.x == -1) {
            lastDirection = -1
            return isCanMove(
                posTile,
                listOf(presetDirection.LEFT_DOWN + presetDirection.LEFT + presetDirection.RIGHT).flatten(),
                game,
                eating
            )
        }

        if (lastDirection == -1)
            return isCanMove(
                posTile,
                listOf(presetDirection._0D) + presetDirection.LEFT + presetDirection.RIGHT,
                game, eating
            )

        return isCanMove(
            posTile,
            listOf(presetDirection._0D) + presetDirection.RIGHT + presetDirection.LEFT,
            game, eating
        )
    }

    private fun isCanMove(
        posTile: Vector,
        listDirections: List<List<Vector>>,
        game: Game,
        eating: () -> Unit

    ): List<Vector> {
        for (directions in listDirections)
            if (isCanDirectionsAndSetCharacterEat(
                    posTile,
                    directions,
                    game,
                    eating = eating
                )
            )
                return directions
        return listOf(Vector(0, 0))
    }

    fun isCanDirectionsAndSetCharacterEat(
        posTile: Vector,
        directions: List<Vector>,
        game: Game,
        isDestroy: Boolean = (0..100).shuffled().first() < PROBABILITY_EAT_PERCENT,
        eating: () -> Unit
    ): Boolean {
        val result = mutableListOf<Vector>()
        var currentVector = Vector(0, 0)

        directions.forEach { direction ->
            currentVector += direction
            val checkingPosition = posTile + currentVector

            if (game.isOutside(checkingPosition))
                return false

            result += direction

            if (game.isNotFree(checkingPosition)) {
                val isCanEatHorizontally = currentVector.y == 0 && isDestroy
                if (isCanEatHorizontally) {
                    eating()
                    return true
                }
                return false
            }
        }

        return true
    }

}