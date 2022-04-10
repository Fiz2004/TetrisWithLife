package com.fiz.tetriswithlife.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.fiz.tetriswithlife.data.NameRepository
import com.fiz.tetriswithlife.databinding.ActivityOptionsBinding

class OptionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOptionsBinding
    private var nameRepository: NameRepository?=null
    private var name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptionsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        bind()
        bindListener()
    }

    private fun init() {
        nameRepository=NameRepository(applicationContext)
        loadInfo()
    }

    private fun bind() {
        binding.nameEditText.setText(name)
    }

    private fun bindListener() {
        binding.nameEditText.doOnTextChanged { text, start, before, count ->
            name = text.toString()
        }

        binding.exitButton.setOnClickListener {
            val data = Intent()
            data.putExtra("name", name)
            setResult(RESULT_OK, data)
            finish()
        }
    }


    override fun onStop() {
        super.onStop()
        saveInfo()
    }

    private fun loadInfo() {
        name=nameRepository?.loadInfo()?:name
    }

    private fun saveInfo() {
        nameRepository?.saveInfo(name)
    }
}