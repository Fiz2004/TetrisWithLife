package com.fiz.tetriswithlife.gameScreen.game.character

data class PresetDirection(
    val L: Character.Companion.Direction = Character.Companion.Direction.Left,
    val R: Character.Companion.Direction = Character.Companion.Direction.Right,
    val D: Character.Companion.Direction = Character.Companion.Direction.Down,
    val U: Character.Companion.Direction = Character.Companion.Direction.Up,
    val _0: Character.Companion.Direction = Character.Companion.Direction.Stop,
    val _0D: List<Character.Companion.Direction> = listOf(D),
    val RD: List<Character.Companion.Direction> = listOf(D, R),
    val LD: List<Character.Companion.Direction> = listOf(D, L),
    val R0: List<Character.Companion.Direction> = listOf(R),
    val L0: List<Character.Companion.Direction> = listOf(L),
    val RU: List<Character.Companion.Direction> = listOf(U, R),
    val LU: List<Character.Companion.Direction> = listOf(U, L),
    val RUU: List<Character.Companion.Direction> = listOf(U, U, R),
    val LUU: List<Character.Companion.Direction> = listOf(U, U, L),
    val LEFT_DOWN: List<List<Character.Companion.Direction>> = listOf(_0D, LD, RD),
    val RIGHT_DOWN: List<List<Character.Companion.Direction>> = listOf(_0D, RD, LD),
    val LEFT: List<List<Character.Companion.Direction>> = listOf(L0, LU, LUU),
    val RIGHT: List<List<Character.Companion.Direction>> = listOf(R0, RU, RUU),
)