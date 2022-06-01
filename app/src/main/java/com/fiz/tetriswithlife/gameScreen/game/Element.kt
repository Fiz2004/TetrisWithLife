package com.fiz.tetriswithlife.gameScreen.game

import com.fiz.tetriswithlife.gameScreen.game.character.Character
import java.io.Serializable

data class Element(
    val background: Int,
    var block: Int = 0,
    var status: StatusElement = StatusElement.Whole
) : Serializable {

    fun setZero() {
        block = 0
        status = StatusElement.Whole
    }

    fun setElement(element: Element) {
        block = element.block
        status = element.status
    }

    fun changeStatus(move: Character.Companion.Direction, value: Int) {
        status = when {
            move == Character.Companion.Direction.Left -> StatusElement.Right(value)
            move == Character.Companion.Direction.Right -> StatusElement.Left(value)
            move == Character.Companion.Direction.Down -> StatusElement.Up(value)
            else -> throw Exception("Error: incorrect value function getDirectionEat ${move.value.x} ${move.value.y}")
        }
    }

    companion object {

        // TODO Разнести логику повреждений в игре от ее показа
        sealed class StatusElement : Serializable {
            class Left(var damage: Int) : StatusElement()

            class Right(var damage: Int) : StatusElement()

            class Up(var damage: Int) : StatusElement()

            object Whole : StatusElement()
        }
    }
}
