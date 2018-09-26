package com.cmfspay.ble.ble

import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.os.ParcelUuid
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import com.cmfspay.ble.BaseActivity
import com.cmfspay.ble.R
import com.cmfspay.ble.util.Constants
import kotlinx.android.synthetic.main.activity_ble_server.*
import java.nio.charset.Charset
import java.util.*

class BleServerActivity : BaseActivity() {

    val TAG = javaClass.simpleName

    val UUID_SERVER = UUID.fromString(Constants.UUID_SERVER_STR)
    val UUID_CHAR_READ = UUID.fromString(Constants.UUID_CHAR_READ_STR)
    val UUID_CHAR_WRITE = UUID.fromString(Constants.UUID_CHAR_WRITE_STR)

    var mBluetoothManager: BluetoothManager? = null
    var mLeAdvertiser: BluetoothLeAdvertiser? = null
    var mAdapter: BluetoothAdapter? = null
    var mBluetoothGattServer: BluetoothGattServer? = null

    val advertiseSettings: AdvertiseSettings by lazy<AdvertiseSettings> {
        AdvertiseSettings.Builder()
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(true)
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .build()
    }

    val advertiseData: AdvertiseData by lazy {
        AdvertiseData.Builder()
                .addManufacturerData(1, byteArrayOf(23, 33))
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(true)
                .build()
    }

    val scanResponse: AdvertiseData by lazy {
        AdvertiseData.Builder()
                .addManufacturerData(2, byteArrayOf(66, 66)) //设备厂商数据，自定义
                .addServiceUuid(ParcelUuid.fromString(Constants.UUID_SERVER_STR)) //服务UUID
                .addServiceData(ParcelUuid.fromString(Constants.UUID_SERVER_STR), byteArrayOf(2)) //服务数据，自定义
                .build()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_ble_server
    }

    override fun init() {
        initBle()
        initListener()
    }

    private fun initListener() {

        btn_start_ble_server.setOnClickListener {
            mLeAdvertiser?.startAdvertising(advertiseSettings, advertiseData, scanResponse, advertiseCallback)
        }

    }

    private fun initBle() {
        if (mBluetoothManager == null) {
            mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        }

        mAdapter = mBluetoothManager?.adapter

        if (mAdapter == null) {
            Toast.makeText(this@BleServerActivity, "not support bluetooth", Toast.LENGTH_SHORT).show()
            finish()
        }

        mLeAdvertiser = mAdapter?.bluetoothLeAdvertiser

        val service = BluetoothGattService(UUID.fromString(Constants.UUID_SERVER_STR),
                BluetoothGattService.SERVICE_TYPE_PRIMARY)

        //读
        val characteristicRead = BluetoothGattCharacteristic(UUID_CHAR_READ,
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ)
        characteristicRead.addDescriptor(BluetoothGattDescriptor(UUID_CHAR_READ,
                BluetoothGattCharacteristic.PERMISSION_WRITE))
        service.addCharacteristic(characteristicRead)

        //写
        val characteristicWrite = BluetoothGattCharacteristic(UUID_CHAR_WRITE,
                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE)
        service.addCharacteristic(characteristicWrite)

        if (mBluetoothManager != null) {
            mBluetoothGattServer = mBluetoothManager?.openGattServer(this, mGattServerCallback)
        }
        mBluetoothGattServer?.addService(service)

    }

    val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            Log.e(TAG, "advertise failed")
        }

        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            super.onStartSuccess(settingsInEffect)
            Log.e(TAG, "advertise success")
        }
    }

    val mGattServerCallback = object : BluetoothGattServerCallback() {

        override fun onDescriptorReadRequest(device: BluetoothDevice?, requestId: Int, offset: Int, descriptor: BluetoothGattDescriptor?) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor)

            Log.e(TAG, "onCharacteristicReadRequest ${device?.name} ${device?.address} ")
            Log.e(TAG, "onCharacteristicReadRequest requestId = $requestId offset = $offset ")
            Log.e(TAG, "onCharacteristicReadRequest cdescriptorUUID = ${descriptor?.uuid}")
            val response = "DESC_" + (Math.random() * 100).toInt() //模拟数据
            mBluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
                    response.toByteArray(Charset.forName("utf-8"))) // 响应客户端
        }

        override fun onNotificationSent(device: BluetoothDevice?, status: Int) {
            super.onNotificationSent(device, status)
            Log.e(TAG, "onCharacteristicReadRequest ${device?.name} ${device?.address} status = $status")
        }

        override fun onMtuChanged(device: BluetoothDevice?, mtu: Int) {
            super.onMtuChanged(device, mtu)
            Log.e(TAG, "onCharacteristicReadRequest ${device?.name} ${device?.address} mtu = $mtu")
        }

        override fun onPhyUpdate(device: BluetoothDevice?, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyUpdate(device, txPhy, rxPhy, status)
        }

        override fun onExecuteWrite(device: BluetoothDevice?, requestId: Int, execute: Boolean) {
            super.onExecuteWrite(device, requestId, execute)
            Log.e(TAG, "onCharacteristicReadRequest ${device?.name} ${device?.address} ")
            Log.e(TAG, "onCharacteristicReadRequest requestId = $requestId execute = $execute ")
        }

        override fun onCharacteristicWriteRequest(device: BluetoothDevice?, requestId: Int, characteristic: BluetoothGattCharacteristic?, preparedWrite: Boolean, responseNeeded: Boolean, offset: Int, value: ByteArray?) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value)

            Log.e(TAG, "onCharacteristicReadRequest ${device?.name} ${device?.address} ")
            Log.e(TAG, "onCharacteristicReadRequest requestId = $requestId offset = $offset ")
            Log.e(TAG, "onCharacteristicReadRequest characteristicUUID = ${characteristic?.uuid}")
            mBluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value)

        }

        override fun onCharacteristicReadRequest(device: BluetoothDevice?, requestId: Int, offset: Int, characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic)

            Log.e(TAG, "onCharacteristicReadRequest ${device?.name} ${device?.address} ")
            Log.e(TAG, "onCharacteristicReadRequest requestId = $requestId offset = $offset ")
            Log.e(TAG, "onCharacteristicReadRequest characteristicUUID = ${characteristic?.uuid}")
            val response = "CHAR_" + (Math.random() * 100).toInt() //模拟数据
            mBluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, response.toByteArray(Charset.forName("utf-8")))

        }

        override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "BluetoothDevice CONNECTED: ${device?.name} ${device?.address} $status $newState")
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "BluetoothDevice DISCONNECTED: ${device?.name} ${device?.address} $status $newState")
                //Remove device from any active subscriptions
            }
        }

        override fun onPhyRead(device: BluetoothDevice?, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyRead(device, txPhy, rxPhy, status)
        }

        override fun onDescriptorWriteRequest(device: BluetoothDevice?, requestId: Int, descriptor: BluetoothGattDescriptor?, preparedWrite: Boolean, responseNeeded: Boolean, offset: Int, value: ByteArray?) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value)
            Log.e(TAG, "onCharacteristicReadRequest ${device?.name} ${device?.address} ")
            Log.e(TAG, "onCharacteristicReadRequest requestId = $requestId offset = $offset ")
            Log.e(TAG, "onCharacteristicReadRequest cdescriptorUUID = ${descriptor?.uuid}")
            Log.e(TAG, "onCharacteristicReadRequest value = ${value.toString()}")
            mBluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);// 响应客户端
            if (BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE.toString() == value.toString()) { //是否开启通知
                val characteristic = descriptor?.getCharacteristic()
                Thread(Runnable {
                    for (i in 1..5) {
                        SystemClock.sleep(3000);
                        val response = "CHAR_" + (Math.random() * 100).toInt() //模拟数据
                        characteristic?.setValue(response)
                        mBluetoothGattServer?.notifyCharacteristicChanged(device, characteristic, false)
                    }
                }).start()
            }

        }

        override fun onServiceAdded(status: Int, service: BluetoothGattService?) {
            super.onServiceAdded(status, service)
            Log.e(TAG, "onServiceAdded $status ${service?.uuid}")
        }
    }
}
