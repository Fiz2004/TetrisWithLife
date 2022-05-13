package com.fiz.tetriswithlife.game.data

import android.content.SharedPreferences

class RecordRepository(private val sharedPreferences: SharedPreferences) {

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