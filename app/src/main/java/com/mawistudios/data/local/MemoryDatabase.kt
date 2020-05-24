package com.mawistudios.data.local

import android.content.Context
import io.objectbox.BoxStore
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

object ObjectBox {
    lateinit var boxStore: BoxStore
        private set

    fun init(context: Context) {
        boxStore = MyObjectBox.builder().androidContext(context.applicationContext).build()
    }
}

@Entity
data class Sensor(
    @Id var id: Long = 0,
    var connectionParams: String? = null,
    var name: String
)

enum class DataPointType {
    HEARTHRATE_BPM, WHEELREVS_KMH, WHEELREVS_DISTANCE, CRANKREVS_CADENCE
}

@Entity
data class SensorData(
    @Id var id: Long = 0,
    var time: Date,
    var dataPointType: String,
    var dataPoint: Double
) {
    //lateinit var sensor: ToOne<Sensor>
}

