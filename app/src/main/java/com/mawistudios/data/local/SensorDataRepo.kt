package com.mawistudios.data.local

import com.mawistudios.app.model.DataPointType
import com.mawistudios.app.model.SensorData
import com.mawistudios.app.model.SensorData_
import io.objectbox.kotlin.boxFor
import io.objectbox.query.Query

interface ISensorDataRepo : IBaseRepo<SensorData> {
    fun currentHearthRate(): SensorData?
    fun currentDistance(): SensorData?
    fun currentSpeed(): SensorData?
    fun currentCadence(): SensorData?
}

class SensorDataRepo : BaseRepo<SensorData>(ObjectBox.boxStore.boxFor()), ISensorDataRepo {
    private fun queryByDataPoint(dataPointType: String): Query<SensorData> {
        return box.query().run {
            equal(
                SensorData_.dataPointType,
                dataPointType
            )
            orderDesc(SensorData_.time)
        }.build()
    }

    override fun currentHearthRate(): SensorData? {
        return queryByDataPoint(DataPointType.HEARTHRATE_BPM.name).findFirst()
    }

    override fun currentDistance(): SensorData? {
        return queryByDataPoint(DataPointType.WHEELREVS_DISTANCE.name).findFirst()
    }

    override fun currentSpeed(): SensorData? {
        return queryByDataPoint(DataPointType.WHEELREVS_KMH.name).findFirst()
    }

    override fun currentCadence(): SensorData? {
        return queryByDataPoint(DataPointType.CRANKREVS_CADENCE.name).findFirst()
    }
}