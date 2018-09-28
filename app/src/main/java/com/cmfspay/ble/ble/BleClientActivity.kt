package com.cmfspay.ble.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.cmfspay.ble.BaseActivity
import com.cmfspay.ble.R
import com.cmfspay.ble.classic.BleDeviceAdapter
import com.cmfspay.ble.util.Constants
import kotlinx.android.synthetic.main.activity_ble_client.*

class BleClientActivity : BaseActivity(), BleDeviceAdapter.OnItemClickListener {

    private var mScanning = false
    private val mdevices = mutableListOf<BluetoothDevice>()
    lateinit var mAdapter : BleDeviceAdapter

    private val mHandler = @SuppressLint("HandlerLeak") object : Handler() {
        override fun handleMessage(msg : Message?) {
            super.handleMessage(msg)
        }
    }

    override fun getLayoutId() : Int {
        return R.layout.activity_ble_client
    }

    override fun init() {
        initRv()
        initListener()
        showLoading()
        startScan(true)
    }

    private fun initListener() {
        activity_ble_client_srl.setOnRefreshListener {
            startScan(true)
        }
        mAdapter.setOnItemClickListener(this)
    }

    override fun onItemClick(device : BluetoothDevice?, position : Int) {
        if (mScanning) {
            startScan(false)
        }
        val intent = Intent(this@BleClientActivity, BleDeviceActivity::class.java)
        intent.putExtra(Constants.EXTRA_DEVICE, device)
        startActivity(intent)
    }

    private fun initRv() {
        mAdapter = BleDeviceAdapter(this, mdevices)
        activity_ble_client_rv.layoutManager = LinearLayoutManager(this)
        activity_ble_client_rv.adapter = mAdapter
    }

    fun startScan(scan : Boolean) {
        when (scan) {
            true  -> {
                if (mScanning) mBluetoothAdapter?.stopLeScan(mLeScanCallback)
                mHandler.postDelayed({
                    mScanning = false
                    mBluetoothAdapter?.stopLeScan(mLeScanCallback)
                    hideLoading()
                    activity_ble_client_srl.isRefreshing = false
                }, Constants.SCAN_PERIOD)
                mScanning = true
                mBluetoothAdapter?.startLeScan(mLeScanCallback)
            }
            false -> {
                mScanning = false
                mBluetoothAdapter?.stopLeScan(mLeScanCallback)
                hideLoading()
                activity_ble_client_srl.isRefreshing = false
            }
        }
    }

    private val mLeScanCallback = BluetoothAdapter.LeScanCallback { device, _, _ ->
        runOnUiThread {
            if (! mdevices.contains(device)) {
                mdevices.add(device)
                mAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
        menuInflater.inflate(R.menu.menu_ble_client, menu)
        return true
    }

    override fun onOptionsItemSelected(item : MenuItem?) : Boolean {
        when (item?.itemId) {
            R.id.menu_scan -> {
                if (mScanning) {
                    Toast.makeText(this, "scanning,please wait.", Toast.LENGTH_SHORT).show()
                } else {
                    startScan(true)
                }
            }
        }
        return true
    }

}
