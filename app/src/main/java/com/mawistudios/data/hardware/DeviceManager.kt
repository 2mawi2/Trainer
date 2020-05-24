package com.mawistudios.data.hardware

import com.mawistudios.app.log
import com.wahoofitness.connector.HardwareConnector
import com.wahoofitness.connector.conn.connections.params.ConnectionParams
import com.wahoofitness.connector.listeners.discovery.DiscoveryListener

class DeviceManager(var connector: HardwareConnector) :
    DiscoveryListener {
    private var sensorManager: SensorManager

    init {
        sensorManager = SensorManager()
    }

    var devices: MutableList<ConnectionParams> = mutableListOf()

    override fun onDiscoveredDeviceRssiChanged(device: ConnectionParams, rssi: Int) {
        log("rssi changed of device: ${device.name}")
        devices.forEach {
            if (it.name == device.name && it.networkType == device.networkType) {
                it.rssi = rssi
            }
        }
    }

    override fun onDiscoveredDeviceLost(device: ConnectionParams) {
        log("undiscovered device: ${device.name}")
        devices.removeIf { it.name == device.name && it.sensorType == device.sensorType }
    }

    override fun onDeviceDiscovered(device: ConnectionParams) {
        log("discovered device ${device.name}")

        devices.add(device)

        connector.requestSensorConnection(device, sensorManager)

    }

}