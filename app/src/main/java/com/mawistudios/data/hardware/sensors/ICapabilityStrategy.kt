package com.mawistudios.data.hardware.sensors

import com.wahoofitness.connector.conn.connections.SensorConnection

interface ICapabilityStrategy {
    fun handleData(connection: SensorConnection)
}


