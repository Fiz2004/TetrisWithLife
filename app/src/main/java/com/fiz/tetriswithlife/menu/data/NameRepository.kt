package com.fiz.tetriswithlife.menu.data

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NameRepository @Inject constructor(private val sharedPreferences: SharedPreferences) {
    fun loadInfo(): String? {
        return sharedPreferences
            .getString("name", "")
    }

    fun saveInfo(name: String) {
        sharedPreferences
            .edit()
            .putString("name", name)
            .apply()
    }

}