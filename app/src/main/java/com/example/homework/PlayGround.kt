package com.example.homework

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.homework.databinding.ActivityPlayGroundBinding

private lateinit var binding: ActivityPlayGroundBinding

class PlayGround : AppCompatActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private var mPendingIntent: PendingIntent? = null
    private var playInform = mutableMapOf<String,Pair<String,Int>>()
    private var check = 0
    private var player1 = ""
    private var player2 = "bank"
    private var give_get = false
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayGroundBinding.inflate(layoutInflater)
        setContentView(binding.root)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        mPendingIntent = PendingIntent.getActivity(this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
        var bundle = intent.extras
        var Inform = bundle?.getBundle("Inform")
        var setting = bundle?.getBundle("Setting")
        var ID = bundle?.getStringArray("ID")
        var beginMoney = setting?.getInt("beginMoney")
        var reStart = setting?.getInt("reStart")
        var players = setting?.getInt("players")
        for(i in ID!!) {
            playInform[i] = Pair(Inform?.getString(i)!!, beginMoney!!)
        }

        binding.Bank.setOnClickListener{ binding.Player2.text = "銀行";check = 0;player2 = "bank" }
        binding.Get.setOnClickListener { binding.Choose.text = "拿";give_get = true}
        binding.Give.setOnClickListener{ binding.Choose.text = "給";give_get = false}
        binding.Pass.setOnClickListener{
            if(player1 == "")
                Toast.makeText(this,"請刷卡",Toast.LENGTH_LONG).show()
            else {
                playInform[player1] = Pair(playInform[player1]!!.first, playInform[player1]!!.second + reStart!!)
                binding.TotalMoney.text = playInform[player1]!!.first+"剩"+playInform[player1]!!.second.toString()
                if(playInform[player1]!!.second < 0) binding.Zero.text = "已破產!!!"
                else binding.Zero.text = ""
            }
        }
        binding.Ready.setOnClickListener {
            var money = binding.Money.text.toString()
            when{
                player1 == "" -> Toast.makeText(this,"請刷卡",Toast.LENGTH_LONG).show()
                money   == "" -> Toast.makeText(this,"請輸入金額",Toast.LENGTH_LONG).show()
                else -> {
                    when{
                        give_get         -> {
                            if(player2 == "All"){
                                playInform[player1] = Pair(playInform[player1]!!.first, playInform[player1]!!.second + (money.toInt()*2))
                                for ((k,v) in playInform){
                                    playInform[k] = Pair(v.first,v.second - money.toInt())
                                }
                            }else if(player2 == "bank"){
                                    playInform[player1] = Pair(playInform[player1]!!.first, playInform[player1]!!.second + money.toInt())
                            }else {
                                playInform[player1] = Pair(playInform[player1]!!.first, playInform[player1]!!.second + money.toInt())
                                playInform[player2] = Pair(playInform[player2]!!.first, playInform[player1]!!.second - money.toInt())
                            }
                            binding.TotalMoney.text = playInform[player1]!!.first+"剩"+playInform[player1]!!.second.toString()
                            if(playInform[player1]!!.second < 0) binding.Zero.text = "已破產!!!"
                            else binding.Zero.text = ""
                        }
                        !give_get        -> {
                            if(player2 == "All"){
                                playInform[player1] = Pair(playInform[player1]!!.first, playInform[player1]!!.second - (money.toInt()*players!!))
                                for ((k,v) in playInform){
                                    playInform[k] = Pair(v.first,v.second + money.toInt())
                                }
                            }else if(player2 == "bank"){
                                playInform[player1] = Pair(playInform[player1]!!.first, playInform[player1]!!.second - money.toInt())
                            }else {
                                playInform[player1] = Pair(playInform[player1]!!.first, playInform[player1]!!.second - money.toInt())
                                playInform[player2] = Pair(playInform[player2]!!.first, playInform[player2]!!.second + money.toInt())
                            }
                            binding.TotalMoney.text = playInform[player1]!!.first + "剩" + playInform[player1]!!.second.toString()
                            if (playInform[player1]!!.second < 0) binding.Zero.text = "已破產!!!"
                            else binding.Zero.text = ""
                        }
                    }

                }
            }
        }
        binding.All.setOnClickListener {
            binding.Player2.text = "所有人"
            player2 = "All"
        }

    }
    override fun onResume() {
        super.onResume()
        nfcAdapter!!.enableForegroundDispatch(this, mPendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        if (nfcAdapter != null) {
            nfcAdapter!!.disableForegroundDispatch(this)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            var readID = ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID))
            binding.Zero.text = ""
            binding.TotalMoney.text = playInform[readID]!!.first+"剩"+playInform[readID]!!.second.toString()
            if(playInform[readID]!!.second < 0) binding.Zero.text = "已破產!!!"

            when{
                check%2 ==0 -> {
                    player1 = readID
                    binding.Player1.text = playInform[readID]!!.first
                }
                check%2 ==1 -> {
                    player2 = readID
                    binding.Player2.text = playInform[readID]!!.first
                }
            }
            check++
        }
    }

    private fun ByteArrayToHexString(inarray: ByteArray?): String {
        var i: Int
        var input: Int
        val hex = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F")
        var out = ""
        var j = 0
        while (j < inarray!!.size) {
            input = inarray[j].toInt() and 0xff
            i = input shr 4 and 0x0f
            out += hex[i]
            i = input and 0x0f
            out += hex[i]
            ++j
        }
        return out
    }
}