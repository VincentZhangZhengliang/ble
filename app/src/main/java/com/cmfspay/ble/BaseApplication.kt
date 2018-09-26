package com.cmfspay.ble

import android.app.Application
import com.cmfspay.ble.util.LogUtil2
import kotlin.properties.Delegates

class BaseApplication : Application() {

    companion object {
        var instance: BaseApplication by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        LogUtil2.getInstance(this).start()
    }

}