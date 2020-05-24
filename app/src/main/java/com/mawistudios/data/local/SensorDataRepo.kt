package com.mawistudios.data.local

import io.objectbox.kotlin.boxFor
import io.objectbox.query.Query

object SensorDataRepo : BaseRepo<SensorData>(ObjectBox.boxStore.boxFor()) {
    private fun queryByDataPoint(dataPointType: String): Query<SensorData> {
        return box.query().run {
            equal(
                SensorData_.dataPointType,
                dataPointType
            )
            orderDesc(SensorData_.time)
        }.build()
    }

    fun currentHearthRate(): SensorData? {
        return queryByDataPoint(DataPointType.HEARTHRATE_BPM.name).findFirst()
    }

    fun currentDistance(): SensorData? {
        return queryByDataPoint(DataPointType.WHEELREVS_DISTANCE.name).findFirst()
    }

    fun currentSpeed(): SensorData? {
        return queryByDataPoint(DataPointType.WHEELREVS_KMH.name).findFirst()
    }

    fun currentCadence(): SensorData? {
        return queryByDataPoint(DataPointType.CRANKREVS_CADENCE.name).findFirst()
    }
}