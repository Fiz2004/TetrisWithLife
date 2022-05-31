package com.fiz.tetriswithlife.gameScreen.game

import java.io.Serializable

private const val NUMBER_IMAGES_BACKGROUND = 16

data class Grid(
    val width: Int,
    val height: Int,
    val valueFon: () -> Int = { (0 until NUMBER_IMAGES_BACKGROUND).shuffled().first() },
    var space: List<List<Element>> = List(height) {
        List(width) {
            Element(valueFon())
        }
    }
) : Serializable