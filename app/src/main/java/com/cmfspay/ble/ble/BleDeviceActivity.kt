package com.cmfspay.ble.ble

import android.bluetooth.*
import android.util.Log
import android.widget.ListAdapter
import com.cmfspay.ble.BaseActivity
import com.cmfspay.ble.R
import com.cmfspay.ble.util.Constants
import kotlinx.android.synthetic.main.activity_ble_device.*

//展示device的services uuid等信息
class BleDeviceActivity : BaseActivity() {

    lateinit var mDevice : BluetoothDevice
    lateinit var mAdapter : MyAdapter
    var mServices = arrayListOf<BluetoothGattService>()

    override fun getLayoutId() : Int {
        return R.layout.activity_ble_device
    }

    override fun init() {
        initListener()
        mDevice = intent.getParcelableExtra(Constants.EXTRA_DEVICE)
        Log.e("TAG", mDevice.address)
        val remoteDevice = mBluetoothAdapter?.getRemoteDevice(mDevice.address)
        val bluetoothGatt = remoteDevice?.connectGatt(this, false, mGattCallBack)
        mAdapter = MyAdapter(this, mServices)
        activity_ble_device_elv.setAdapter(mAdapter)
    }

    private fun initListener() {

        activity_ble_device_elv.setOnChildClickListener { expandableListView, view, i, i1, l ->

            true
        }

    }

    val mGattCallBack = object : BluetoothGattCallback() {

        override fun onCharacteristicRead(gatt : BluetoothGatt?, characteristic : BluetoothGattCharacteristic?, status : Int) {
            super.onCharacteristicRead(gatt, characteristic, status)
        }

        override fun onCharacteristicWrite(gatt : BluetoothGatt?, characteristic : BluetoothGattCharacteristic?, status : Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
        }


        override fun onMtuChanged(gatt : BluetoothGatt?, mtu : Int, status : Int) {
            super.onMtuChanged(gatt, mtu, status)
        }

        override fun onCharacteristicChanged(gatt : BluetoothGatt?, characteristic : BluetoothGattCharacteristic?) {
            super.onCharacteristicChanged(gatt, characteristic)
        }

        /**
         * 连接状态改变的回调
         */
        override fun onConnectionStateChange(gatt : BluetoothGatt?, status : Int, newState : Int) {
            super.onConnectionStateChange(gatt, status, newState)
            when (newState) {
                BluetoothProfile.STATE_CONNECTING    -> {
                    Log.e("TAG", "STATE_CONNECTING")
                }
                BluetoothProfile.STATE_CONNECTED     -> {
                    Log.e("TAG", "STATE_CONNECTED")
                    //连接成功，寻找外设的服务
                    gatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTING -> {
                    Log.e("TAG", "STATE_DISCONNECTING")
                }
                BluetoothProfile.STATE_DISCONNECTED  -> {
                    Log.e("TAG", "STATE_DISCONNECTED")
                    //连接断开
                }
            }
        }

        /**
         * 连接成功后寻找服务的回调
         */
        override fun onServicesDiscovered(gatt : BluetoothGatt?, status : Int) {
            super.onServicesDiscovered(gatt, status)
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    Log.e("TAG", "GATT_SUCCESS")
                    //发现外设所有的服务
                    val services = gatt?.services as List<BluetoothGattService>
                    mServices.clear()
                    mServices.addAll(services)
                    mAdapter.notifyDataSetChanged()
                    //                    //通过遍历服务，比对服务的UUID查找到需要的服务
                    //                    services?.forEachIndexed { _, service ->
                    //                        val serviceUuid = service.uuid
                    //                        if (serviceUuid == UUID.fromString("服务UUID")) {
                    //                            //通过服务，拿到所有的characritics
                    //                            val characteristics = service.characteristics
                    //                            characteristics.forEach { characteristic ->
                    //                                val charUuid = characteristic.uuid
                    //                                if (charUuid == UUID.fromString("特征UUID")) {
                    //                                    //设置通知  当服务端对应的UUID发生变化时发送通知  设置通知也时一次写操作  writeDescriptor 放在最后面
                    //                                    gatt.setCharacteristicNotification(characteristic, true)
                    //                                    val descriptor = characteristic.getDescriptor(UUID.fromString("descriptor的UUID"))
                    //                                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    //                                    gatt.writeDescriptor(descriptor)
                    //                                }
                    //                            }
                    //                        }
                    //                    }
                }
            }

        }

    }


}
