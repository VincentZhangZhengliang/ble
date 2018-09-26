package com.cmfspay.ble.classic

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cmfspay.ble.R

class BleDeviceAdapter(var context: Context, var data: MutableList<BluetoothDevice>?) : RecyclerView.Adapter<BleDeviceAdapter.ViewHolder>() {

    private var mlistener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_ble_device, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = data?.get(position)
        holder.tvDeviceName.text = device?.name ?: "unknown"
        holder.tvDeviceAddress.text = device?.address ?: ""
        holder.clRoot.setOnClickListener {
            mlistener?.onItemClick(device, position)
        }
    }

    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val tvDeviceName = itemview.findViewById<TextView>(R.id.tv_device_name)
        val tvDeviceAddress = itemview.findViewById<TextView>(R.id.tv_device_address)
        val clRoot = itemview.findViewById<ConstraintLayout>(R.id.cl_root)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mlistener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(device: BluetoothDevice?, position: Int)
    }

}