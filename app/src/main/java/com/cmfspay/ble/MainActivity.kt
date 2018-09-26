package com.cmfspay.ble

import android.content.Intent
import android.util.Log
import com.cmfspay.ble.ble.BleClientActivity
import com.cmfspay.ble.ble.BleServerActivity
import com.cmfspay.ble.classic.ClassicClientActivity
import com.cmfspay.ble.classic.ClassicServerActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun init() {
        tv_ble_client.setOnClickListener {
            startActivity(Intent(this, BleClientActivity::class.java))
            Log.e("Tag", "tv_ble_client")
        }
        tv_ble_server.setOnClickListener {
            startActivity(Intent(this, BleServerActivity::class.java))
            Log.e("Tag", "tv_ble_server")
        }
        tv_classic_client.setOnClickListener {
            startActivity(Intent(this, ClassicClientActivity::class.java))
            Log.e("Tag", "tv_classic_client")
        }
        tv_classic_server.setOnClickListener {
            startActivity(Intent(this, ClassicServerActivity::class.java))
            Log.e("Tag", "tv_classic_server")
        }
    }

}
