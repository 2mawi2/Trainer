package com.mawistudios.data.hardware.sensors

import com.mawistudios.TrainingSessionObservable
import com.mawistudios.app.log
import com.mawistudios.data.hardware.sensors.CrankRevsSensorStrategy
import com.mawistudios.data.hardware.sensors.HearthRateSensorStrategy
import com.mawistudios.data.hardware.sensors.UnknownSensorStrategy
import com.mawistudios.data.hardware.sensors.WheelRevsSensorStrategy
import com.wahoofitness.connector.HardwareConnectorEnums.SensorConnectionError
import com.wahoofitness.connector.HardwareConnectorEnums.SensorConnectionState
import com.wahoofitness.connector.capabilities.Capability.CapabilityType
import com.wahoofitness.connector.conn.connections.SensorConnection


class SensorManager : SensorConnection.Listener {
    private fun SensorConnectionState.asString(): String = when (this) {
        SensorConnectionState.DISCONNECTED -> "DISCONNECTED"
        SensorConnectionState.CONNECTING -> "CONNECTING"
        SensorConnectionState.CONNECTED -> "CONNECTED"
        SensorConnectionState.DISCONNECTING -> "DISCONNECTING"
    }

    override fun onSensorConnectionStateChanged(
        connection: SensorConnection,
        state: SensorConnectionState
    ) {
        log("sensor connection state changed: ${connection.connectionParams.name} $state")

        TrainingSessionObservable.onSensorConnectionStateChanged(
            connection.deviceName,
            state.asString()
        )
    }

    override fun onSensorConnectionError(
        connection: SensorConnection,
        error: SensorConnectionError
    ) {
        log("sensor connection error")
    }

    override fun onNewCapabilityDetected(
        connection: SensorConnection,
        type: CapabilityType
    ) {
        when (type) {
            CapabilityType.Heartrate -> HearthRateSensorStrategy()
            CapabilityType.WheelRevs -> WheelRevsSensorStrategy()
            CapabilityType.CrankRevs -> CrankRevsSensorStrategy()
            else -> UnknownSensorStrategy()
        }.handleData(connection)
    }
}

