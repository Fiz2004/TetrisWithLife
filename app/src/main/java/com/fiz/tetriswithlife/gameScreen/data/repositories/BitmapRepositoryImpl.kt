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

    override val bmpFon: Bitmap =
        BitmapFactory.decodeResource(context.resources, R.drawable.background)

    override val bmpCharacter: Bitmap =
        BitmapFactory.decodeResource(context.resources, R.drawable.character)

    override val bmpKv: List<Bitmap> by lazy {
        (1..NUMBER_IMAGES_FIGURE).mapTo(mutableListOf<Bitmap>()) {
            BitmapFactory.decodeResource(
                context.resources, context.resources.getIdentifier(
                    "block$it",
                    "drawable", context.packageName
                )
            )
        }
    }

}


