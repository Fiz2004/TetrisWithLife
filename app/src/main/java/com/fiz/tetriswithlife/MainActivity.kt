package com.fiz.tetriswithlife

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var newGameButton: Button
    private lateinit var optionsButton: Button
    private lateinit var exitButton: Button
    private lateinit var nameTextView: TextView
    private val mStartForResult = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            val name = intent!!.getStringExtra("name")
            nameTextView.text = "Name: $name"
        } else {
            nameTextView.text = "Name: "
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
            val name = arguments["name"].toString()
            nameTextView.text = "Name: $name"
        }

        newGameButton.setOnClickListener { view: View ->
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        optionsButton.setOnClickListener { view: View ->
            val intent = Intent(this, OptionsActivity::class.java)

            mStartForResult.launch(intent)
        }

        exitButton.setOnClickListener {
            finish()
        }
    }
}