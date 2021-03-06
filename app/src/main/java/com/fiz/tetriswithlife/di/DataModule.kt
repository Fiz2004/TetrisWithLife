package com.fiz.tetriswithlife.di

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.fiz.tetriswithlife.gameScreen.data.repositories.BitmapRepositoryImpl
import com.fiz.tetriswithlife.gameScreen.data.repositories.RecordRepositoryImpl
import com.fiz.tetriswithlife.gameScreen.domain.repositories.BitmapRepository
import com.fiz.tetriswithlife.gameScreen.domain.repositories.RecordRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val NAME_SHARED_PREFERENCES = "data"

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            NAME_SHARED_PREFERENCES, AppCompatActivity.MODE_PRIVATE
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModuleInterface {

    @Singleton
    @Binds
    abstract fun provideRecordRepository(RecordRepository: RecordRepositoryImpl): RecordRepository

    @Singleton
    @Binds
    abstract fun provideBitmapRepository(BitmapRepository: BitmapRepositoryImpl): BitmapRepository
}