package com.fiz.tetriswithlife.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.fiz.tetriswithlife.R
import com.fiz.tetriswithlife.data.NameRepository
import com.fiz.tetriswithlife.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var nameRepository: NameRepository? = null
    var name: String = ""
    private val mStartForResult = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            intent?.let {
                name = it.getStringExtra("name") ?: name
            }
        }
        updateUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        bindListener()
        updateUI()
    }

    private fun updateUI() {
        binding.nameTextView.text = resources.getString(R.string.name, name)
    }

    private fun init() {
        nameRepository = NameRepository(applicationContext)
        loadInfo()
    }

    private fun bindListener() {
        binding.newGameButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        binding.optionsButton.setOnClickListener {
            val intent = Intent(this, OptionsActivity::class.java)
            mStartForResult.launch(intent)
        }

        binding.exitButton.setOnClickListener {
            finish()
        }
    }

    private fun loadInfo() {
        name = nameRepository?.loadInfo() ?: name
    }

    override fun onStop() {
        super.onStop()
        saveInfo()
    }

    private fun saveInfo() {
        nameRepository?.saveInfo(name)
    }
}