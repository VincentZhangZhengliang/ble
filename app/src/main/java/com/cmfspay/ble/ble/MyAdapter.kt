package com.cmfspay.ble.ble

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.cmfspay.ble.R

/**
 * @author Python
 * @date 2018/9/27
 * @desc
 */
class MyAdapter(var context : Context, var services : List<BluetoothGattService>?) : BaseExpandableListAdapter() {

    //获取分组的个数
    override fun getGroupCount() : Int {
        Log.e("MyAdapter", services?.size.toString())
        return services?.size ?: 0
    }

    //获取指定分组中的子选项的个数
    override fun getChildrenCount(p0 : Int) : Int {
        return services?.get(p0)?.characteristics?.size ?: 0
    }

    //获取指定的分组数据
    override fun getGroup(p0 : Int) : BluetoothGattService? {
        return services?.get(p0)
    }

    //获取指定分组中的指定子选项数据
    override fun getChild(p0 : Int, p1 : Int) : BluetoothGattCharacteristic? {
        return services?.get(p0)?.characteristics?.get(p1)
    }

    //获取指定分组的ID, 这个ID必须是唯一的
    override fun getGroupId(p0 : Int) : Long {
        return p0.toLong()
    }

    //获取子选项的ID, 这个ID必须是唯一的
    override fun getChildId(p0 : Int, p1 : Int) : Long {
        return p1.toLong()
    }

    //分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们。
    override fun hasStableIds() : Boolean {
        return true
    }

    //指定位置上的子元素是否可选中
    override fun isChildSelectable(p0 : Int, p1 : Int) : Boolean {
        return true
    }

    override fun getGroupView(p0 : Int, p1 : Boolean, p2 : View?, p3 : ViewGroup?) : View {
        val holder : GroupViewHolder
        var view : View
        if (p2 == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_ble_service, p3, false)
            holder = GroupViewHolder(view)
            view.tag = holder
        } else {
            view = p2
            holder = view.tag as GroupViewHolder
        }
        holder.tv_service?.text = services?.get(p0)?.uuid.toString()
        holder.tv_service_uuid?.text = services?.get(p0)?.uuid.toString()
        return view
    }

    override fun getChildView(p0 : Int, p1 : Int, p2 : Boolean, p3 : View?, p4 : ViewGroup?) : View {
        val holder : ChildViewHolder
        var view : View
        if (p3 == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_ble_characristic, p4, false)
            holder = ChildViewHolder(view)
            view.tag = holder
        } else {
            view = p3
            holder = view.tag as ChildViewHolder
        }
        //properties 可能同时含有可读（2）可写（8）可通知（16）
        Log.e("Joker", services?.get(p0)?.characteristics?.get(p1)?.properties?.toInt().toString())
        holder.tv_characritics?.text = services?.get(p0)?.characteristics?.get(p1)?.uuid.toString()
        when (services?.get(p0)?.characteristics?.get(p1)?.properties) {
            BluetoothGattCharacteristic.PROPERTY_READ   -> {
                holder.tv_characristics_uuid?.text = "可读"
            }
            BluetoothGattCharacteristic.PROPERTY_WRITE  -> {
                holder.tv_characristics_uuid?.text = "可写"
            }
            BluetoothGattCharacteristic.PROPERTY_NOTIFY -> {
                holder.tv_characristics_uuid?.text = "可通知"
            }
            10                                          -> {
                holder.tv_characristics_uuid?.text = "可读可写"
            }
            18                                          -> {
                holder.tv_characristics_uuid?.text = "可读可通知"
            }
            24                                          -> {
                holder.tv_characristics_uuid?.text = "可写可通知"
            }
            26                                          -> {
                holder.tv_characristics_uuid?.text = "可读可写可通知"
            }
        }
        holder.tv_characristics_uuid?.text = services?.get(p0)?.characteristics?.get(p1)?.uuid.toString()
        return view
    }

    class GroupViewHolder(itemView : View?) {
        val tv_service = itemView?.findViewById<TextView>(R.id.tv_service)
        val tv_service_uuid = itemView?.findViewById<TextView>(R.id.tv_service_uuid)
    }

    class ChildViewHolder(itemView : View?) {
        val tv_characritics = itemView?.findViewById<TextView>(R.id.tv_characritics)
        val tv_characristics_uuid = itemView?.findViewById<TextView>(R.id.tv_characristics_uuid)
    }

}