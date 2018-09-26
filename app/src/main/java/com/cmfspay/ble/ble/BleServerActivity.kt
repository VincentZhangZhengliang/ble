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

    private val UUID_SERVER = UUID.fromString(Constants.UUID_SERVER_STR)
    private val UUID_CHAR_READ = UUID.fromString(Constants.UUID_CHAR_READ_STR)
    private val UUID_CHAR_WRITE = UUID.fromString(Constants.UUID_CHAR_WRITE_STR)
    private val UUID_DESCRIPTOR = UUID.fromString(Constants.UUID_DESCRIPTOR_STR)

    private var mBluetoothManager : BluetoothManager? = null
    private var mLeAdvertiser : BluetoothLeAdvertiser? = null
    private var mAdapter : BluetoothAdapter? = null
    var mBluetoothGattServer : BluetoothGattServer? = null

    private val advertiseSettings : AdvertiseSettings by lazy<AdvertiseSettings> {
        AdvertiseSettings.Builder()
            .setTimeout(0)         //设置广播的最长时间
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)   //设置广播的信号强度
            .setConnectable(true)  //设置是否可以连接。
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)  //设置广播的模式，低功耗，平衡和低延迟三种模式;
            .build()
    }

    private val advertiseData : AdvertiseData by lazy {
        AdvertiseData.Builder()
            .addManufacturerData(1, byteArrayOf(23, 33))  //添加厂商信息，
            .setIncludeDeviceName(true)   //是否广播设备名称。
            .setIncludeTxPowerLevel(true)  //是否广播信号强度
            .build()
    }

    private val scanResponse : AdvertiseData by lazy {
        AdvertiseData.Builder()
            .addManufacturerData(2, byteArrayOf(66, 66))                           //设备厂商数据，自定义
            .addServiceUuid(ParcelUuid.fromString(Constants.UUID_SERVER_STR))                   //添加服务进广播，即对外广播本设备拥有的服务。   测试是否可以添加多个
            .addServiceData(ParcelUuid.fromString(Constants.UUID_SERVER_STR), byteArrayOf(2))   //添加服务进广播，即对外广播本设备拥有的服务。
            .build()
    }

    private val gattService : BluetoothGattService by lazy {
        BluetoothGattService(UUID_SERVER, BluetoothGattService.SERVICE_TYPE_PRIMARY)
    }
    //读特征
    private val characteristicRead : BluetoothGattCharacteristic by lazy {
        BluetoothGattCharacteristic(UUID_CHAR_READ, BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_NOTIFY, BluetoothGattCharacteristic.PERMISSION_READ)
    }
    //写特征
    private val characteristicWrite : BluetoothGattCharacteristic by lazy {
        BluetoothGattCharacteristic(UUID_CHAR_WRITE, BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_NOTIFY or BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_WRITE)
    }
    //读 descriptor
    private val readDescriptor : BluetoothGattDescriptor by lazy {
        BluetoothGattDescriptor(UUID_DESCRIPTOR, BluetoothGattCharacteristic.PERMISSION_WRITE)
    }

    override fun getLayoutId() : Int {
        return R.layout.activity_ble_server
    }

    override fun init() {
        initBle()
        initListener()
    }

    private fun initListener() {
        btn_start_ble_advertise.setOnClickListener {
            mLeAdvertiser?.startAdvertising(advertiseSettings, advertiseData, scanResponse, advertiseCallback)
        }
        btn_close_ble_advertise.setOnClickListener {
            mLeAdvertiser?.stopAdvertising(advertiseCallback)
        }
    }

    private fun initBle() {
        if (mBluetoothManager == null) {
            mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        }
        mAdapter = mBluetoothManager?.adapter
        if (mAdapter == null) {
            Toast.makeText(this@BleServerActivity, "not support bluetooth", Toast.LENGTH_SHORT)
                .show()
            finish()
        }
        mLeAdvertiser = mAdapter?.bluetoothLeAdvertiser
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartFailure(errorCode : Int) {
            super.onStartFailure(errorCode)
            Log.e(TAG, "advertise failed")
            when (errorCode) {
                AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED      -> {
                    //广播已经启动
                    Log.e(TAG, "ADVERTISE_FAILED_ALREADY_STARTED")
                }
                AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE       -> {
                    //数据太大
                    Log.e(TAG, "ADVERTISE_FAILED_DATA_TOO_LARGE")
                }
                AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED  -> {
                    //不支持
                    Log.e(TAG, "ADVERTISE_FAILED_FEATURE_UNSUPPORTED")
                }
                AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR       -> {
                    //内部错误
                    Log.e(TAG, "ADVERTISE_FAILED_INTERNAL_ERROR")
                }
                AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS -> {
                    //开启了太多广播
                    Log.e(TAG, "ADVERTISE_FAILED_TOO_MANY_ADVERTISERS")
                }
            }
        }

        override fun onStartSuccess(settingsInEffect : AdvertiseSettings?) {
            super.onStartSuccess(settingsInEffect)
            Log.e(TAG, "advertise success")
            //广播开启成功后初始化gatt服务
            initGatt()
        }
    }

    private fun initGatt() {
        //添加读
        characteristicRead.addDescriptor(readDescriptor)
        gattService.addCharacteristic(characteristicRead)
        //添加写
        gattService.addCharacteristic(characteristicWrite)

        if (mBluetoothManager != null) {
            mBluetoothGattServer = mBluetoothManager?.openGattServer(this, mGattServerCallback)
        }

        mBluetoothGattServer?.addService(gattService)
    }

    private val mGattServerCallback = object : BluetoothGattServerCallback() {

        /**
         * 1.连接状态发生变化时
         * @param device
         * @param status
         * @param newState
         */
        override fun onConnectionStateChange(device : BluetoothDevice?, status : Int, newState : Int) {
            super.onConnectionStateChange(device, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "BluetoothDevice CONNECTED: ${device?.name} ${device?.address} $status $newState")
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "BluetoothDevice DISCONNECTED: ${device?.name} ${device?.address} $status $newState")
                //Remove device from any active subscriptions
            }
        }

        /**
         * 2.是否有远程设备连接进来
         */
        override fun onServiceAdded(status : Int, service : BluetoothGattService?) {
            super.onServiceAdded(status, service)
            Log.e(TAG, "onServiceAdded $status ${service?.uuid}")
        }

        /**
         * 3.远程设备请求读取本地的descriptor 需要调用sendResponse来结束这个请求
         */
        override fun onDescriptorReadRequest(device : BluetoothDevice?, requestId : Int, offset : Int, descriptor : BluetoothGattDescriptor?) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor)
            Log.e(TAG, "onCharacteristicReadRequest ${device?.name} ${device?.address} ")
            Log.e(TAG, "onCharacteristicReadRequest requestId = $requestId offset = $offset ")
            Log.e(TAG, "onCharacteristicReadRequest cdescriptorUUID = ${descriptor?.uuid}")
            val response = "DESC_" + (Math.random() * 100).toInt() //模拟数据
            //返回给远程设备 请求成功BluetoothGatt.GATT_SUCCESS
            mBluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, response.toByteArray(Charset.forName("utf-8"))) // 响应客户端
        }

        /**
         * 3.远程设备请求向本地的descriptor写入数据 需要调用sendResponse来结束这个请求
         */
        override fun onDescriptorWriteRequest(device : BluetoothDevice?, requestId : Int, descriptor : BluetoothGattDescriptor?, preparedWrite : Boolean, responseNeeded : Boolean, offset : Int, value : ByteArray?) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value)
            Log.e(TAG, "onCharacteristicReadRequest ${device?.name} ${device?.address} ")
            Log.e(TAG, "onCharacteristicReadRequest requestId = $requestId offset = $offset ")
            Log.e(TAG, "onCharacteristicReadRequest cdescriptorUUID = ${descriptor?.uuid}")
            Log.e(TAG, "onCharacteristicReadRequest value = ${value.toString()}")
            // 响应客户端 请求成功 BluetoothGatt.GATT_SUCCESS
            mBluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value)
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

        /**
         * 4.远程设备请求读取本地的characteristic 需要调用sendResponse来结束这个请求
         */
        override fun onCharacteristicReadRequest(device : BluetoothDevice?, requestId : Int, offset : Int, characteristic : BluetoothGattCharacteristic?) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic)

            Log.e(TAG, "onCharacteristicReadRequest ${device?.name} ${device?.address} ")
            Log.e(TAG, "onCharacteristicReadRequest requestId = $requestId offset = $offset ")
            Log.e(TAG, "onCharacteristicReadRequest characteristicUUID = ${characteristic?.uuid}")
            val response = "CHAR_" + (Math.random() * 100).toInt() //模拟数据
            //响应客户端，请求成功 BluetoothGatt.GATT_SUCCESS  并返回数据response
            mBluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, response.toByteArray(Charset.forName("utf-8")))
        }

        /**
         * 5.远程设备请求向本地的characteristic写入数据 需要调用sendResponse来结束这个请求
         */
        override fun onCharacteristicWriteRequest(device : BluetoothDevice?, requestId : Int, characteristic : BluetoothGattCharacteristic?, preparedWrite : Boolean, responseNeeded : Boolean, offset : Int, value : ByteArray?) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value)
            Log.e(TAG, "onCharacteristicReadRequest ${device?.name} ${device?.address} ")
            Log.e(TAG, "onCharacteristicReadRequest requestId = $requestId offset = $offset ")
            Log.e(TAG, "onCharacteristicReadRequest characteristicUUID = ${characteristic?.uuid}")
            //处理远程设备写入的数据
            dealWriteRequest(device, requestId, characteristic, value)
            ////响应客户端，请求成功 BluetoothGatt.GATT_SUCCESS  并返回数据response
            mBluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value)
        }

        /**
         * 6.本地向远程设备发送notification/indication的回调，如果有多个notification/indication要发送，需要等上一个notification/indication发送完成
         */
        override fun onNotificationSent(device : BluetoothDevice?, status : Int) {
            super.onNotificationSent(device, status)
            Log.e(TAG, "onCharacteristicReadRequest ${device?.name} ${device?.address} status = $status")
        }

        /**
         *
         */
        override fun onMtuChanged(device : BluetoothDevice?, mtu : Int) {
            super.onMtuChanged(device, mtu)
            Log.e(TAG, "onCharacteristicReadRequest ${device?.name} ${device?.address} mtu = $mtu")
        }

        override fun onPhyUpdate(device : BluetoothDevice?, txPhy : Int, rxPhy : Int, status : Int) {
            super.onPhyUpdate(device, txPhy, rxPhy, status)
        }

        override fun onExecuteWrite(device : BluetoothDevice?, requestId : Int, execute : Boolean) {
            super.onExecuteWrite(device, requestId, execute)
            Log.e(TAG, "onCharacteristicReadRequest ${device?.name} ${device?.address} ")
            Log.e(TAG, "onCharacteristicReadRequest requestId = $requestId execute = $execute ")
        }

        override fun onPhyRead(device : BluetoothDevice?, txPhy : Int, rxPhy : Int, status : Int) {
            super.onPhyRead(device, txPhy, rxPhy, status)
        }

    }

    /**
     * 处理远程设备写入的数据
     * @param device  远程连接设备
     * @param requestId  请求id
     * @param value   远程设备写入的值
     */
    private fun dealWriteRequest(device : BluetoothDevice?, requestId : Int, characteristic : BluetoothGattCharacteristic?, value : ByteArray?) {
        mBluetoothGattServer?.notifyCharacteristicChanged(device, characteristic, false)
    }
}
