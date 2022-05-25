package com.fiz.tetriswithlife.gameScreen.domain.models

data class Controller(
    var down: Boolean = false,
    var up: Boolean = false,
    var left: Boolean = false,
    var right: Boolean = false,
)