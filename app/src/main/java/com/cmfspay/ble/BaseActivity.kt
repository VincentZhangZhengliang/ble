package com.cmfspay.ble

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.cmfspay.ble.dialog.LoadingDialog

abstract class BaseActivity : AppCompatActivity() {

    public val REQUEST_ENABLE_BT: Int = 100

    public val mBluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        init()
    }

    abstract fun init()

    abstract fun getLayoutId(): Int

    override fun onResume() {
        super.onResume()
        //是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "not support bluetooth", Toast.LENGTH_SHORT).show()
            finish()
        }
        //蓝牙是否打开
        mBluetoothAdapter?.takeIf { !it.isEnabled }?.apply {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "bluetooth open success", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "bluetooth open failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    var loadingDialog: LoadingDialog? = null

    open fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.getInstance()
        }
        loadingDialog?.show(fragmentManager, "")
    }

    open fun hideLoading() {
        loadingDialog?.dismiss()
    }

}
