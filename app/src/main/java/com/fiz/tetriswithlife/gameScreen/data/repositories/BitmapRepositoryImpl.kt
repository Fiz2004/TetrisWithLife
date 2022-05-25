package com.fiz.tetriswithlife.gameScreen.data.repositories

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.fiz.tetriswithlife.R
import com.fiz.tetriswithlife.gameScreen.domain.repositories.BitmapRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val NUMBER_IMAGES_FIGURE = 5

@Singleton
class BitmapRepositoryImpl @Inject constructor(@ApplicationContext context: Context) :
    BitmapRepository {

    override val bmpFon: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.fon)
    override val bmpCharacter: Bitmap =
        BitmapFactory.decodeResource(context.resources, R.drawable.character)
    override val bmpKv: List<Bitmap> by lazy {
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


