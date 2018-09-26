package com.cmfspay.ble.ble

import android.bluetooth.BluetoothDevice
import com.cmfspay.ble.BaseActivity
import com.cmfspay.ble.R
import com.cmfspay.ble.util.Constants

//展示device的services uuid等信息
class BleDeviceActivity : BaseActivity() {

    lateinit var mDevice: BluetoothDevice

    override fun init() {
        mDevice = intent.getParcelableExtra(Constants.EXTRA_DEVICE)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_ble_device
    }

}
