package com.mawistudios

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.mawistudios.data.hardware.HardwareManager
import com.mawistudios.data.hardware.IHardwareManager
import javax.inject.Inject

class SensorService : Service() {
    lateinit var hardwareManager: IHardwareManager

    override fun onCreate() {
        super.onCreate()
        hardwareManager = HardwareManager(this)
    }

    fun startDiscovery() = hardwareManager.discover()

    fun stopDiscovering() = hardwareManager.stopDiscovering()

    inner class LocalBinder : Binder() {
        fun getService(): SensorService = this@SensorService
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder? = binder

    override fun onDestroy() {
        super.onDestroy()
        hardwareManager.shutdown()
    }
}
