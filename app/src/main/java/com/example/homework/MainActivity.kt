package com.example.homework

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.homework.databinding.ActivityMainBinding


private lateinit var binding: ActivityMainBinding

class MainActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.start.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }
}
