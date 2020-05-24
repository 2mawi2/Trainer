package com.mawistudios.data.hardware

import com.mawistudios.TrainingSessionObservable
import com.mawistudios.app.log
import com.mawistudios.data.local.*
import com.wahoofitness.connector.HardwareConnectorEnums.SensorConnectionError
import com.wahoofitness.connector.HardwareConnectorEnums.SensorConnectionState
import com.wahoofitness.connector.capabilities.Capability.CapabilityType
import com.wahoofitness.connector.capabilities.CrankRevs
import com.wahoofitness.connector.capabilities.Heartrate
import com.wahoofitness.connector.capabilities.WheelRevs
import com.wahoofitness.connector.conn.connections.SensorConnection
import java.util.*

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
            CapabilityType.Heartrate -> onNewHearthRateCapability(connection)
            CapabilityType.WheelRevs -> onNewWheelRevsCapability(connection)
            CapabilityType.CrankRevs -> onNewCrankRevsCapability(connection)
            CapabilityType.BatteryPercent -> log("new capability battery percent ${connection.deviceName}")
            CapabilityType.Battery -> log("new capability battery ${connection.deviceName}")
            else -> OnUnknowCapability(connection)
        }
    }

    private fun onNewCrankRevsCapability(connection: SensorConnection) {
        log("new crank rev capability")
        val crackRevs = connection.getCurrentCapability(CapabilityType.CrankRevs) as CrankRevs
        crackRevs.addListener { data ->
            log(data.toString())
            SensorDataRepo.add(
                SensorData(
                    dataPoint = data.crankSpeed.asRpm(),
                    time = Date(data.timeMs),
                    dataPointType = DataPointType.CRANKREVS_CADENCE.name
                )
            )
        }
    }


    private fun onNewWheelRevsCapability(connection: SensorConnection) {
        log("new wheel rev capability")
        val wheelRevs = connection.getCurrentCapability(CapabilityType.WheelRevs) as WheelRevs
        val wheelCircumference = 2.105 //m 700 x 25C

        wheelRevs.addListener { data ->
            log(data.toString())
            val bikeSpeedKMH = data.wheelSpeed.asRpm() * 60 * wheelCircumference / 1000
            val totalBikeDistanceM = data.wheelRevs * wheelCircumference

            SensorDataRepo.add(
                SensorData(
                    dataPoint = bikeSpeedKMH,
                    time = Date(data.timeMs),
                    dataPointType = DataPointType.WHEELREVS_KMH.name
                )
            )

            SensorDataRepo.add(
                SensorData(
                    dataPoint = totalBikeDistanceM,
                    time = Date(data.timeMs),
                    dataPointType = DataPointType.WHEELREVS_DISTANCE.name
                )
            )

            TrainingSessionObservable.onTrainingDataChanged()
        }
    }

    private fun OnUnknowCapability(connection: SensorConnection) {
        log("current capabilities ${connection.currentCapabilities}")
    }

    private fun onNewHearthRateCapability(connection: SensorConnection) {
        log("new hearth rate capability")
        val hearthRate = connection.getCurrentCapability(CapabilityType.Heartrate) as Heartrate

        hearthRate.addListener(object : Heartrate.Listener {
            override fun onHeartrateData(data: Heartrate.Data) {
                log(data.toString())
                SensorDataRepo.add(
                    SensorData(
                        dataPoint = data.heartrate.asEventsPerMinute(),
                        time = Date(data.timeMs),
                        dataPointType = DataPointType.HEARTHRATE_BPM.name
                    )
                )
                TrainingSessionObservable.onTrainingDataChanged()
            }

            override fun onHeartrateDataReset() {}
        })
    }
}

