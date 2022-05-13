package com.fiz.tetriswithlife

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.fiz.tetriswithlife.game.data.RecordRepository
import com.fiz.tetriswithlife.menu.data.NameRepository

private const val NAME_SHARED_PREFERENCES = "data"

class App : Application() {
    val sharedPreferences: SharedPreferences by lazy {
        applicationContext.getSharedPreferences(
            NAME_SHARED_PREFERENCES, AppCompatActivity.MODE_PRIVATE
        )
    }

    val recordRepository: RecordRepository by lazy {
        RecordRepository(sharedPreferences)
    }

    val nameRepository: NameRepository by lazy {
        NameRepository(sharedPreferences)
    }

}