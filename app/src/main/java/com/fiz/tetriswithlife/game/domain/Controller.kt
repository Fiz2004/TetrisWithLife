package com.fiz.tetriswithlife.game.domain

data class Controller(
    val down: Boolean = false,
    val up: Boolean = false,
    val left: Boolean = false,
    val right: Boolean = false,
)