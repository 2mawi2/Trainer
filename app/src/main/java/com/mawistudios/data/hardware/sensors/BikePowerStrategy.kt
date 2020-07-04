package com.mawistudios.data.hardware.sensors

import com.mawistudios.app.log
import com.mawistudios.app.model.DataPointType
import com.mawistudios.app.model.SensorData
import com.mawistudios.data.hardware.TrainingSessionObservable
import com.mawistudios.data.local.ISensorDataRepo
import com.wahoofitness.connector.capabilities.BikePower
import com.wahoofitness.connector.capabilities.Capability
import com.wahoofitness.connector.conn.connections.SensorConnection
import java.util.*

class BikePowerStrategy(private val sensorDataRepo: ISensorDataRepo) : ICapabilityStrategy {
    override fun handleData(connection: SensorConnection) {
        log("new power capability")
        val hearthRate = connection.getCurrentCapability(Capability.CapabilityType.BikePower) as BikePower

        hearthRate.addListener { data ->
            log(data.toString())
            sensorDataRepo.save(
                SensorData(
                    dataPoint = data.powerWatts,
                    time = Date(data.timeMs),
                    dataPointType = DataPointType.POWER_WATT.name
                )
            )
            TrainingSessionObservable.onTrainingDataChanged()
        }
    }
}
