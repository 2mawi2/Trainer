package com.mawistudios.data.hardware.sensors

import com.mawistudios.app.log
import com.wahoofitness.connector.conn.connections.SensorConnection

class UnknownSensorStrategy : ICapabilityStrategy {
    override fun handleData(connection: SensorConnection) {
        log("current capabilities ${connection.currentCapabilities}")
    }
}