package com.mawistudios.data.hardware.sensors

import com.mawistudios.TrainingSessionObservable
import com.mawistudios.app.log
import com.mawistudios.data.local.DataPointType
import com.mawistudios.data.local.SensorData
import com.mawistudios.data.local.SensorDataRepo
import com.wahoofitness.connector.capabilities.Capability
import com.wahoofitness.connector.capabilities.WheelRevs
import com.wahoofitness.connector.conn.connections.SensorConnection
import java.util.*

class WheelRevsSensorStrategy : ICapabilityStrategy {
    override fun handleData(connection: SensorConnection) {
        log("new wheel rev capability")
        val wheelRevs =
            connection.getCurrentCapability(Capability.CapabilityType.WheelRevs) as WheelRevs
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
                ),
                SensorData(
                    dataPoint = totalBikeDistanceM,
                    time = Date(data.timeMs),
                    dataPointType = DataPointType.WHEELREVS_DISTANCE.name
                )
            )

            TrainingSessionObservable.onTrainingDataChanged()
        }
    }
}