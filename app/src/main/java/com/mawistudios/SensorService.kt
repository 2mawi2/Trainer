package com.mawistudios

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.mawistudios.data.hardware.HardwareManager
import com.mawistudios.data.hardware.IHardwareManager
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class SensorService : Service() {
    private val hardwareManager: IHardwareManager by inject { parametersOf(this) }

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
