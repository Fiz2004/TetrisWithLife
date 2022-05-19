package com.fiz.tetriswithlife.game.di

import com.fiz.tetriswithlife.game.domain.Controller
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {

    @Provides
    fun provideController(): Controller {
        return Controller()
    }
}