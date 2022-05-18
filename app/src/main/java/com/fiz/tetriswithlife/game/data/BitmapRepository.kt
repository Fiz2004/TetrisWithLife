package com.fiz.tetriswithlife.game.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.fiz.tetriswithlife.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val NUMBER_IMAGES_FIGURE = 5

@Singleton
class BitmapRepository @Inject constructor(@ApplicationContext context: Context) {

    val bmpFon: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.fon)
    val bmpCharacter: Bitmap =
        BitmapFactory.decodeResource(context.resources, R.drawable.character)
    val bmpKv: List<Bitmap> by lazy {
        val result: MutableList<Bitmap> = mutableListOf()
        for (i in 1..NUMBER_IMAGES_FIGURE)
            result += BitmapFactory.decodeResource(
                context.resources, context.resources.getIdentifier(
                    "kvadrat$i",
                    "drawable", context.packageName
                )
            )
        result
    }

}