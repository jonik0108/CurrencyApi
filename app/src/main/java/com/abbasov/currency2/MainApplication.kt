package com.abbasov.currency2

import android.app.Application
import com.onesignal.OneSignal

const val ONESIGNAL_APP_ID = "7d92e959-7fee-4c68-a64c-30c16e28cba4"
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
    }
}