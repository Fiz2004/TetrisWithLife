package com.fiz.tetriswithlife

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var newGameButton: Button
    private lateinit var optionsButton: Button
    private lateinit var exitButton: Button
    private lateinit var nameTextView: TextView
    var name: String = ""
    private val mStartForResult = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            name = intent!!.getStringExtra("name")!!
            nameTextView.text = resources.getString(R.string.name, name)
        } else {
            nameTextView.text = resources.getString(R.string.name, "")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        newGameButton = findViewById(R.id.new_game_main_button)
        optionsButton = findViewById(R.id.options_main_button)
        exitButton = findViewById(R.id.exit_main_button)
        nameTextView = findViewById(R.id.name_main_textview)
        val arguments = intent.extras
        if (arguments != null) {
            name = arguments["name"].toString()
            nameTextView.text = resources.getString(R.string.name, name)
        } else {
            val prefEditor: SharedPreferences = getSharedPreferences(
                "data", Context
                    .MODE_PRIVATE
            )
            name = prefEditor.getString("Name", "") ?: ""
            nameTextView.text = resources.getString(R.string.name, name)
        }

        newGameButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        optionsButton.setOnClickListener {
            val intent = Intent(this, OptionsActivity::class.java)

            mStartForResult.launch(intent)
        }

        exitButton.setOnClickListener {
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        val prefEditor: SharedPreferences.Editor = getSharedPreferences(
            "data", Context
                .MODE_PRIVATE
        ).edit()
        prefEditor.putString("Name", name)
        prefEditor.apply()
    }
}