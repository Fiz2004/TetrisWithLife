package com.fiz.tetriswithlife.game.domain.models.character

import com.fiz.tetriswithlife.game.domain.models.Vector

data class PresetDirection(
    val L: Vector = Vector(-1, 0),
    val R: Vector = Vector(1, 0),
    val D: Vector = Vector(0, 1),
    val U: Vector = Vector(0, -1),
    val _0: Vector = Vector(0, 0),
    val _0D: List<Vector> = listOf(D),
    val RD: List<Vector> = listOf(D, R),
    val LD: List<Vector> = listOf(D, L),
    val R0: List<Vector> = listOf(R),
    val L0: List<Vector> = listOf(L),
    val RU: List<Vector> = listOf(U, R),
    val LU: List<Vector> = listOf(U, L),
    val RUU: List<Vector> = listOf(U, U, R),
    val LUU: List<Vector> = listOf(U, U, L),
    val LEFT_DOWN: List<List<Vector>> = listOf(_0D, LD, RD),
    val RIGHT_DOWN: List<List<Vector>> = listOf(_0D, RD, LD),
    val LEFT: List<List<Vector>> = listOf(L0, LU, LUU),
    val RIGHT: List<List<Vector>> = listOf(R0, RU, RUU),
)