package com.mawistudios.data.hardware

import com.mawistudios.TrainingSessionObservable
import com.mawistudios.app.log
import com.mawistudios.data.local.SensorData
import com.mawistudios.data.local.SensorDataRepo
import com.wahoofitness.connector.HardwareConnectorEnums.SensorConnectionError
import com.wahoofitness.connector.HardwareConnectorEnums.SensorConnectionState
import com.wahoofitness.connector.capabilities.Capability.CapabilityType
import com.wahoofitness.connector.capabilities.Heartrate
import com.wahoofitness.connector.conn.connections.SensorConnection
import java.util.*

class SensorManager : SensorConnection.Listener {
    override fun onSensorConnectionStateChanged(
        connection: SensorConnection,
        state: SensorConnectionState
    ) {
        log("sensor connection state changed: ${connection.connectionParams.name} $state")
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
            CapabilityType.Heartrate -> onNewHearthRateCapability(connection)
            CapabilityType.BatteryPercent -> log("new capability battery percent ${connection.deviceName}")
            CapabilityType.Battery -> log("new capability battery ${connection.deviceName}")
            else -> OnUnknowCapability(connection)
        }
    }

    private fun OnUnknowCapability(connection: SensorConnection) {
        //log("current capabilities ${connection.currentCapabilities}")
    }

    private fun onNewHearthRateCapability(connection: SensorConnection) {
        log("new hearth rate capability")
        val hearthRate = connection.getCurrentCapability(CapabilityType.Heartrate) as Heartrate

        hearthRate.addListener(object : Heartrate.Listener {
            override fun onHeartrateData(data: Heartrate.Data) {
                //log(data.toString())
                SensorDataRepo.add(mapSensorData(data, connection))
                TrainingSessionObservable.onNewHearthRateData()
            }

            override fun onHeartrateDataReset() {}
        })
    }

    private fun mapSensorData(data: Heartrate.Data, connection: SensorConnection) = SensorData(
        dataPoint = data.heartrate.asEventsPerMinute(),
        time = Date(data.timeMs)
    )
}

