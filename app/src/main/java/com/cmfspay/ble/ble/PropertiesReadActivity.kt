package com.cmfspay.ble.ble

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.cmfspay.ble.R
import kotlinx.android.synthetic.main.activity_properties_read.*

//读属性操作界面
class PropertiesReadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_properties_read)
        initListener()
    }

    private fun initListener() {
        btn_read.setOnClickListener { }
        btn_open_read_operation.setOnClickListener { }
        btn_close_read_operation.setOnClickListener { }
    }

}
