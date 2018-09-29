package com.cmfspay.ble.dialog

import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.*
import com.cmfspay.ble.R
import com.cmfspay.ble.util.Constants
import kotlinx.android.synthetic.main.dialog_properties.view.*

class PropertiesDialog : DialogFragment() {

    private lateinit var mActivity : AppCompatActivity
    private var mProperties : Int? = 0

    companion object {
        fun getInstance(properties : Int) : PropertiesDialog {
            val bundle = Bundle()
            bundle.putInt(Constants.EXTRA_PROPERTIES, properties)
            val dialog = PropertiesDialog()
            dialog.arguments = bundle
            return dialog
        }
    }

    override fun onAttach(context : Context?) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
        mProperties = arguments?.getInt(Constants.EXTRA_PROPERTIES)
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BottomDialog)
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        val view = inflater?.inflate(R.layout.dialog_properties, container, false) !!
        initview(view)
        initListener(view)
        return view
    }

    private fun initListener(view : View) {
        view.tv_property_read.setOnClickListener {
            val listener = activity as OnPropertiesClickListener
            listener.onPropertiesClick(BluetoothGattCharacteristic.PROPERTY_READ)
            dismiss()
        }
        view.tv_property_write.setOnClickListener {
            val listener = activity as OnPropertiesClickListener
            listener.onPropertiesClick(BluetoothGattCharacteristic.PROPERTY_WRITE)
            dismiss()
        }
        view.tv_property_notify.setOnClickListener {
            val listener = activity as OnPropertiesClickListener
            listener.onPropertiesClick(BluetoothGattCharacteristic.PROPERTY_NOTIFY)
            dismiss()
        }
    }

    private fun initview(view : View) {
        when (mProperties) {
            //可读（2）可写（8）可通知（16）
            2  -> {
                view.tv_property_read.visibility = View.VISIBLE
                view.tv_property_write.visibility = View.GONE
                view.tv_property_notify.visibility = View.GONE
            }
            8  -> {
                view.tv_property_read.visibility = View.GONE
                view.tv_property_write.visibility = View.VISIBLE
                view.tv_property_notify.visibility = View.GONE
            }
            10 -> {
                view.tv_property_read.visibility = View.VISIBLE
                view.tv_property_write.visibility = View.VISIBLE
                view.tv_property_notify.visibility = View.GONE
            }
            16 -> {
                view.tv_property_read.visibility = View.GONE
                view.tv_property_write.visibility = View.GONE
                view.tv_property_notify.visibility = View.VISIBLE
            }
            18 -> {
                view.tv_property_read.visibility = View.VISIBLE
                view.tv_property_write.visibility = View.GONE
                view.tv_property_notify.visibility = View.VISIBLE
            }
            24 -> {
                view.tv_property_read.visibility = View.GONE
                view.tv_property_write.visibility = View.VISIBLE
                view.tv_property_notify.visibility = View.VISIBLE
            }
            26 -> {
                view.tv_property_read.visibility = View.VISIBLE
                view.tv_property_write.visibility = View.VISIBLE
                view.tv_property_notify.visibility = View.VISIBLE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        val window = dialog.window
        window !!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isCancelable = false
        dialog.setCanceledOnTouchOutside(false)
        val windowParams = window.attributes
        windowParams.dimAmount = 0.5f
        windowParams.gravity = Gravity.CENTER
        windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = windowParams
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    interface OnPropertiesClickListener {
        fun onPropertiesClick(properties : Int)
    }

}