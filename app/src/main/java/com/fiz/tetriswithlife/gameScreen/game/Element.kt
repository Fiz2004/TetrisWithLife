package com.fiz.tetriswithlife.gameScreen.game

import com.fiz.tetriswithlife.gameScreen.game.character.Character
import java.io.Serializable

data class Element(val background: Int) : Serializable {

    var block: Int = 0
        private set

    var status: StatusElement = StatusElement.Whole
        private set

    fun setZero() {
        block = 0
        status = StatusElement.Whole
    }

    fun setElement(element: Element) {
        block = element.block
        status = element.status
    }


    fun setBlock(value: Int) {
        block = value
    }

    fun changeStatus(move: Character.Companion.Direction, value: Double) {
        status = when (move) {
            Character.Companion.Direction.Left -> StatusElement.Right(value)
            Character.Companion.Direction.Right -> StatusElement.Left(value)
            Character.Companion.Direction.Down -> StatusElement.Up(value)
            else -> throw Exception("Error: incorrect value function getDirectionEat ${move.value.x} ${move.value.y}")
        }
    }

    fun fixationCell(block: Int) {
        this.block = block
        status = StatusElement.Whole
    }

    companion object {

        sealed class StatusElement : Serializable {
            class Left(var damage: Double) : StatusElement()
            class Right(var damage: Double) : StatusElement()
            class Up(var damage: Double) : StatusElement()
            object Whole : StatusElement()
        }
    }
}
