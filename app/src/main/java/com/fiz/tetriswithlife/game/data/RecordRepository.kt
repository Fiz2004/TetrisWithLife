package com.fiz.tetriswithlife.game.data

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordRepository @Inject constructor(private val sharedPreferences: SharedPreferences) {
    fun loadRecord(): Int {
        return sharedPreferences
            .getInt("Record", 0)
    }

    fun saveRecord(scores: Int) {
        sharedPreferences
            .edit()
            .putInt("Record", scores)
            .apply()
    }
}