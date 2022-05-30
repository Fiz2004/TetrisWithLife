package com.fiz.tetriswithlife.gameScreen.domain.models.figure

import com.fiz.tetriswithlife.gameScreen.game.Vector
import java.io.Serializable

data class Cell(val vector: Vector, val view: Int) : Serializable {

    fun getCellRotate(): Cell {
        return Cell(
            Vector(
                3 - vector.y,
                vector.x
            ), view
        )
    }

}