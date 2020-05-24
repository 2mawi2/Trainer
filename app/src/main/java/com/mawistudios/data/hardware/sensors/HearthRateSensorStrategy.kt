package com.mawistudios.data.hardware.sensors

import com.mawistudios.TrainingSessionObservable
import com.mawistudios.app.log
import com.mawistudios.data.local.DataPointType
import com.mawistudios.data.local.SensorData
import com.mawistudios.data.local.SensorDataRepo
import com.wahoofitness.connector.capabilities.Capability
import com.wahoofitness.connector.capabilities.Heartrate
import com.wahoofitness.connector.conn.connections.SensorConnection
import java.util.*

class HearthRateSensorStrategy : ICapabilityStrategy {
    override fun handleData(connection: SensorConnection) {
        log("new hearth rate capability")
        val hearthRate =
            connection.getCurrentCapability(Capability.CapabilityType.Heartrate) as Heartrate

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