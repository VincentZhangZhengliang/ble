package com.cmfspay.ble.dialog

import android.app.DialogFragment
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.*
import com.cmfspay.ble.R
import com.cmfspay.ble.util.Constants
import kotlinx.android.synthetic.main.dialog_properties.*

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
        mProperties = arguments.getInt(Constants.EXTRA_PROPERTIES)
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BottomDialog)
    }

    override fun onCreateView(inflater : LayoutInflater?, container : ViewGroup?, savedInstanceState : Bundle?) : View {
        val view = inflater?.inflate(R.layout.dialog_properties, container, false) !!
        initview(view)
        initListener()
        return view
    }

    private fun initListener() {
        tv_property_read.setOnClickListener { }
        tv_property_write.setOnClickListener { }
        tv_property_notify.setOnClickListener { }
    }

    private fun initview(view : View) {
        when (mProperties) {
            //可读（2）可写（8）可通知（16）
            2  -> {
                tv_property_read.visibility = View.VISIBLE
                tv_property_write.visibility = View.GONE
                tv_property_notify.visibility = View.GONE
            }
            8  -> {
                tv_property_read.visibility = View.GONE
                tv_property_write.visibility = View.VISIBLE
                tv_property_notify.visibility = View.GONE
            }
            10 -> {
                tv_property_read.visibility = View.VISIBLE
                tv_property_write.visibility = View.VISIBLE
                tv_property_notify.visibility = View.GONE
            }
            16 -> {
                tv_property_read.visibility = View.GONE
                tv_property_write.visibility = View.GONE
                tv_property_notify.visibility = View.VISIBLE
            }
            18 -> {
                tv_property_read.visibility = View.VISIBLE
                tv_property_write.visibility = View.GONE
                tv_property_notify.visibility = View.VISIBLE
            }
            24 -> {
                tv_property_read.visibility = View.GONE
                tv_property_write.visibility = View.VISIBLE
                tv_property_notify.visibility = View.VISIBLE
            }
            26 -> {
                tv_property_read.visibility = View.VISIBLE
                tv_property_write.visibility = View.VISIBLE
                tv_property_notify.visibility = View.VISIBLE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
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

}