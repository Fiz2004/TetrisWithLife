package com.fiz.tetriswithlife.gameScreen.data.repositories

import android.content.SharedPreferences
import com.fiz.tetriswithlife.gameScreen.domain.repositories.RecordRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordRepositoryImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    RecordRepository {
    override fun loadRecord(): Int {
        return sharedPreferences
            .getInt("Record", 0)
    }

    override fun saveRecord(scores: Int) {
        sharedPreferences
            .edit()
            .putInt("Record", scores)
            .apply()
    }
}

