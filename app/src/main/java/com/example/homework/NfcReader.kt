package com.example.homework

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.os.bundleOf
import com.example.homework.databinding.ActivityNfcReaderBinding
import java.io.Serializable

private lateinit var binding: ActivityNfcReaderBinding

class NfcReader : AppCompatActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private var mPendingIntent: PendingIntent? = null
    private var cardID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNfcReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this,"你的裝置不支援NFC\nfc not support your device.",Toast.LENGTH_LONG).show()
            return
        }
        mPendingIntent = PendingIntent.getActivity(this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
        var old_bundle = intent.extras
        var players = old_bundle?.getInt("players")
        var checkName = mutableSetOf<String?>()
        var checkID = arrayOfNulls<String>(players!!)
        var playerInform = Bundle()
        binding.OK.setOnClickListener {
            var name = binding.Name.text.toString()
            when {
                cardID == null -> Toast.makeText(this,"請刷卡或檢查NFC功能是否開啟",Toast.LENGTH_LONG).show()
                name == "" -> Toast.makeText(this,"請輸入玩家名稱",Toast.LENGTH_LONG).show()
                cardID in checkID -> Toast.makeText(this,"NFC卡片重複",Toast.LENGTH_LONG).show()
                name in checkName -> Toast.makeText(this,"玩家名字重複",Toast.LENGTH_LONG).show()
                else -> {
                    checkID[players!!-1] = cardID!!
                    checkName.add(name)
                    playerInform.putString(cardID,name)
                    cardID = null
                    binding.cardID.text = "(請感應卡片)"
                    binding.Name.text = null
                    players= players?.minus(1)
                }
            }
            if(players == 0){
                var next = Intent(this,PlayGround::class.java)
                var bundle = Bundle()
                bundle.putBundle("Setting",old_bundle)
                bundle.putBundle("Inform",playerInform)
                bundle.putStringArray("ID",checkID)
                next.putExtras(bundle)
                startActivity(next)
            }
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
            cardID = ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID))
            binding.cardID.text = cardID

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


