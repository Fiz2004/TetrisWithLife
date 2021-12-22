package com.fiz.tetriswithlife

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent

class OptionsActivity : AppCompatActivity() {
    private var name: String = ""
    private lateinit var nameTextView: TextView
    private lateinit var nameEditView: EditText
    private lateinit var exitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        nameTextView = findViewById(R.id.name_options_textview)
        nameEditView = findViewById(R.id.name_options_editview)
        exitButton = findViewById(R.id.exit_options_button)

        nameEditView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                name = s.toString()
            }
        })

        exitButton.setOnClickListener {
            val data = Intent()
            data.putExtra("name", nameEditView.text.toString())
            setResult(RESULT_OK, data)
            finish()
        }
    }
}