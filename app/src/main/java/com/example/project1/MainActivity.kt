package com.example.project1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.project1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val nextIntent = Intent(this, SignUpActivity::class.java)
            startActivity(nextIntent)
        }
    }
}