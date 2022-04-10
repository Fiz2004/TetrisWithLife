package com.fiz.tetriswithlife.menu.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.fiz.tetriswithlife.databinding.ActivityOptionsBinding
import com.fiz.tetriswithlife.menu.data.NameRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OptionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOptionsBinding

    @Inject
    lateinit var nameRepository: NameRepository
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
        loadInfo()
    }

    private fun bind() {
        binding.nameEditText.setText(name)
    }

    private fun bindListener() {
        binding.nameEditText.doAfterTextChanged {
            name = it.toString()
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
        name = nameRepository.loadInfo() ?: name
    }

    private fun saveInfo() {
        nameRepository.saveInfo(name)
    }
}