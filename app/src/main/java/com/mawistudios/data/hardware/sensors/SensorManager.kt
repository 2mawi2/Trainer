package com.mawistudios.data.hardware.sensors

import com.mawistudios.data.hardware.TrainingSessionObservable
import com.mawistudios.app.asString
import com.mawistudios.app.log
import com.mawistudios.data.local.ISensorDataRepo
import com.mawistudios.app.model.Sensor
import com.wahoofitness.connector.HardwareConnectorEnums.SensorConnectionError
import com.wahoofitness.connector.HardwareConnectorEnums.SensorConnectionState
import com.wahoofitness.connector.capabilities.Capability.CapabilityType
import com.wahoofitness.connector.conn.connections.SensorConnection


interface ISensorManager : SensorConnection.Listener {
    override fun onSensorConnectionStateChanged(
        connection: SensorConnection,
        state: SensorConnectionState
    )

    override fun onSensorConnectionError(
        connection: SensorConnection,
        error: SensorConnectionError
    )

    override fun onNewCapabilityDetected(
        connection: SensorConnection,
        type: CapabilityType
    )
}

class SensorManager(
    private val sensorDataRepo: ISensorDataRepo
) : ISensorManager {


    override fun onSensorConnectionStateChanged(
        connection: SensorConnection,
        state: SensorConnectionState
    ) {
        log("sensor connection state changed: ${connection.connectionParams.name} $state")

        TrainingSessionObservable.onSensorConnectionStateChanged(
            Sensor(
                state = state.asString(),
                name = connection.deviceName,
                params = connection.connectionParams.serialize()
            )
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
            CapabilityType.Heartrate -> HearthRateSensorStrategy(sensorDataRepo)
            CapabilityType.WheelRevs -> WheelRevsSensorStrategy(sensorDataRepo)
            CapabilityType.CrankRevs -> CrankRevsSensorStrategy(sensorDataRepo)
            else -> UnknownSensorStrategy()
        }.handleData(connection)
    }
}

