package com.mawistudios.data.hardware.sensors

import com.mawistudios.app.log
import com.mawistudios.app.model.DataPointType
import com.mawistudios.data.local.ISensorDataRepo
import com.mawistudios.app.model.SensorData
import com.wahoofitness.connector.capabilities.Capability
import com.wahoofitness.connector.capabilities.CrankRevs
import com.wahoofitness.connector.conn.connections.SensorConnection
import java.util.*

class CrankRevsSensorStrategy(
    private val sensorDataRepo: ISensorDataRepo
) : ICapabilityStrategy {
    override fun handleData(connection: SensorConnection) {
        log("new crank rev capability")
        val crackRevs =
            connection.getCurrentCapability(Capability.CapabilityType.CrankRevs) as CrankRevs
        crackRevs.addListener { data ->
            log(data.toString())
            sensorDataRepo.save(
                SensorData(
                    dataPoint = data.crankSpeed.asRpm(),
                    time = Date(data.timeMs),
                    dataPointType = DataPointType.CRANKREVS_CADENCE.name
                )
            )
        }
    }
}