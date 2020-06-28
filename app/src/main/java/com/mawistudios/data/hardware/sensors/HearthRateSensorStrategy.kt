package com.mawistudios.data.hardware.sensors

import com.mawistudios.data.hardware.TrainingSessionObservable
import com.mawistudios.app.log
import com.mawistudios.app.model.DataPointType
import com.mawistudios.data.local.ISensorDataRepo
import com.mawistudios.app.model.SensorData
import com.wahoofitness.connector.capabilities.Capability
import com.wahoofitness.connector.capabilities.Heartrate
import com.wahoofitness.connector.conn.connections.SensorConnection
import java.util.*

class HearthRateSensorStrategy(val sensorDataRepo: ISensorDataRepo) : ICapabilityStrategy {
    override fun handleData(connection: SensorConnection) {
        log("new hearth rate capability")
        val hearthRate =
            connection.getCurrentCapability(Capability.CapabilityType.Heartrate) as Heartrate

        hearthRate.addListener(object : Heartrate.Listener {
            override fun onHeartrateData(data: Heartrate.Data) {
                log(data.toString())
                sensorDataRepo.add(
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