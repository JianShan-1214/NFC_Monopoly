package com.example.homework

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.homework.databinding.ActivitySettingBinding


private lateinit var binding: ActivitySettingBinding
class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var beginMoney  = 15000
        var reStartMoney = 800
        var player = 2
        val players = arrayListOf("2","3","4","5","6")
        val adapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,players)
        binding.spinner.adapter = adapter
        binding.NEXT.setOnClickListener {
            val begin = binding.beginMoney.text.toString()
            val restart = binding.reStartMoney.text.toString()
            if(begin != "")
                beginMoney = begin.toInt()
            if(restart != "")
                reStartMoney = restart.toInt()
            player = binding.spinner.selectedItem.toString().toInt()
            var bundle = Bundle()
            bundle.putInt("beginMoney",beginMoney)
            bundle.putInt("reStart",reStartMoney)
            bundle.putInt("players",player)
            val intent = Intent(this, NfcReader::class.java)
            intent.putExtras(bundle)
            startActivity(intent)

        }
    }

}