package com.fiz.tetriswithlife.data

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

private const val NAME_SHARED_PREFERENCES = "data"

class NameRepository(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(
        NAME_SHARED_PREFERENCES, AppCompatActivity.MODE_PRIVATE
    )

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