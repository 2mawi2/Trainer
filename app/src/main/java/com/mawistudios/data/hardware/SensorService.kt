package com.mawistudios.data.hardware

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.mawistudios.app.log
import com.mawistudios.app.model.Sensor
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class SensorService : Service() {
    private val hardwareManager: IHardwareManager by inject { parametersOf(this) }
    private var isDiscovering = false

    fun startDiscovery() {
        log("Starting discovery")
        hardwareManager.discover()
        isDiscovering = true
    }

    fun stopDiscovery() {
        if (!isDiscovering) return
        log("Stopping discovery")
        hardwareManager.stopDiscovering()
        isDiscovering = false
    }

    inner class LocalBinder : Binder() {
        fun getService(): SensorService = this@SensorService
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        hardwareManager.shutdown()
    }

    fun connectDevice(sensor: Sensor) {
        hardwareManager.connectDevice(sensor)
    }

}
