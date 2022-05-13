package com.fiz.tetriswithlife.menu.data

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

class NameRepository(private val sharedPreferences: SharedPreferences) {

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