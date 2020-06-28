package com.mawistudios.data.hardware

import android.content.Context
import com.mawistudios.app.log
import com.mawistudios.data.hardware.sensors.ISensorManager
import com.mawistudios.app.model.Sensor
import com.wahoofitness.connector.HardwareConnector
import com.wahoofitness.connector.HardwareConnectorEnums
import com.wahoofitness.connector.HardwareConnectorTypes
import com.wahoofitness.connector.conn.connections.SensorConnection

interface IHardwareManager {
    fun discover()
    fun stopDiscovering()
    fun shutdown()
    fun connectDevice(sensor: Sensor)
}

class HardwareManager constructor(
    private val serviceContext: Context,
    private val sensorManager: ISensorManager
) : IHardwareManager {
    private val discoveryListener: DeviceManager
    private val connector: HardwareConnector

    private var isHardwareReady: Boolean = false
    private var networkType: HardwareConnectorTypes.NetworkType? = null

    private val hardWareListener = object : HardwareConnector.Listener {
        override fun onHardwareConnectorStateChanged(
            type: HardwareConnectorTypes.NetworkType,
            state: HardwareConnectorEnums.HardwareConnectorState
        ) {
            when (state) {
                HardwareConnectorEnums.HardwareConnectorState.HARDWARE_NOT_ENABLED -> {
                    isHardwareReady = false
                    log("Hardware not enabled $type $state")
                }
                HardwareConnectorEnums.HardwareConnectorState.HARDWARE_NOT_SUPPORTED -> {
                    isHardwareReady = false
                    log("Hardware not supported $type $state")
                }
                HardwareConnectorEnums.HardwareConnectorState.HARDWARE_READY -> {
                    isHardwareReady = true
                    networkType = type
                    log("Hardware ready $type $state")
                }
            }
        }

        override fun onFirmwareUpdateRequired(
            sensorConnection: SensorConnection, p1: String, p2: String
        ) {
            log("Firmware update required")
        }
    }

    init {
        connector = HardwareConnector(serviceContext, hardWareListener)
        com.wahoofitness.common.log.Logger.setLogLevel(android.util.Log.VERBOSE)
        discoveryListener = DeviceManager(connector, sensorManager)
    }

    override fun connectDevice(sensor: Sensor) {
        val params = sensor.getParameters()

        if (params != null) {
            connector.requestSensorConnection(params, sensorManager)
            log("connecting sensor: ${sensor.name}")
        } else {
            log("sensor: ${sensor.name} could not be connected")
        }
    }

    override fun discover() {
        startDiscovering()
    }

    private fun startDiscovering() {
        connector.startDiscovery(discoveryListener)
        TrainingSessionObservable.onDiscoveryStarted()
    }

    override fun stopDiscovering() {
        connector.stopDiscovery(discoveryListener)
    }

    override fun shutdown() {
        stopDiscovering()
        connector.shutdown()
    }

}