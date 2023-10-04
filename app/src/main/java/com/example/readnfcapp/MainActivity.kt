package com.example.readnfcapp

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.readnfcapp.databinding.ActivityMainBinding

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.PersistableBundle
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var pendingIntent: PendingIntent
    private lateinit var intentFiltersArray: Array<IntentFilter>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // NfcAdapterの取得
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        // NfcAdapterがnullかどうかのチェック
        if (nfcAdapter == null) {
            // NFC非対応端末の場合の処理
            Log.i("MainActivity", "NFCが有効にできませんでした。")
            return
        }
        else
        {
            Log.i("MainActivity","NFCが有効にできました。")
        }

        // PendingIntentの作成
        // Android 11対応
//        pendingIntent = PendingIntent.getActivity(
//            this, 0,
//            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
//        )
        // 改修後のコード（Android 12対応）
//        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        Log.i("MainActivity","PendingIntentの作成が完了した。")

        // NFC Intentフィルタの設定
        val ndef = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        intentFiltersArray = arrayOf(ndef)
        Log.i("MainActivity","NFC Intentファイタの設定が完了した。")

    }

    override fun onResume() {
        super.onResume()
        Log.i("MainActivity","onResume")
        if(nfcAdapter == null)
            Log.i("MainActivity","nfcAdapter == null")
        else
            Log.i("MainActivity","nfcAdapter != null")
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, null)
    }

    override fun onPause() {
        super.onPause()
        Log.i("MainActivity","onPause")
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Log.i("MainActivity","onCreate")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val tag: Tag? = intent?.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        if (tag != null) {
            // FeliCaタグの読み取り処理をここに書く
            Log.i("MainActivity", "NFC.tag:" + tag.id.toString())

            val id: ByteArray = tag.id
            val tagIdHexString = id.joinToString("") { "%02x".format(it) }
            val techList: Array<String> = tag.techList
            val describeContents: Int = tag.describeContents()
//            val mifareClassic: MifareClassic = MifareClassic.get(tag)

            val textView: TextView = findViewById(R.id.textView)
            textView.text = "NFC.tag.id:" + tagIdHexString +
                    "\nNFC.describeContents:" + tag.describeContents() +
                    "\nNFC.techList:" + techList.joinToString("\n")
        }
        else {
            Log.i("MainActivity", "tag == null")
        }
    }
}