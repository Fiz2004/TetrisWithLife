package com.fiz.tetriswithlife.di

import com.fiz.tetriswithlife.gameScreen.domain.repositories.RecordRepository
import com.fiz.tetriswithlife.gameScreen.game.Game
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GameModule {

    @Provides
    @Singleton
    fun provideGame(recordRepository: RecordRepository): Game {
        return Game(recordRepository = recordRepository)
    }
}