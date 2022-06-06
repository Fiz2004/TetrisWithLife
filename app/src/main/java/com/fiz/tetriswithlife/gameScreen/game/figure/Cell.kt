package com.fiz.tetriswithlife.gameScreen.game.figure

import com.fiz.tetriswithlife.gameScreen.game.Vector
import java.io.Serializable

data class Cell(val vector: Vector, val block: Int) : Serializable {

    val cellRotate
        get() = Cell(
            Vector(
                3 - vector.y,
                vector.x
            ), block
        )

}