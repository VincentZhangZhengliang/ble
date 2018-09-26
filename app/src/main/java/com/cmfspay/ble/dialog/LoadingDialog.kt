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

class LoadingDialog : DialogFragment() {

    private lateinit var mActivity: AppCompatActivity

    companion object {
        fun getInstance(): LoadingDialog {
            return LoadingDialog()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BottomDialog)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater?.inflate(R.layout.dialog_loading, container, false)!!
    }

    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        val window = dialog.window
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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