package com.fiz.tetriswithlife.gameScreen.domain.repositories

import android.graphics.Bitmap

interface BitmapRepository {
    val bmpFon: Bitmap
    val bmpCharacter: Bitmap
    val bmpKv: List<Bitmap>
}