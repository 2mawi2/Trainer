package com.mawistudios.app.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

@Entity
data class SensorData(
    @Id var id: Long = 0,
    var time: Date,
    var dataPointType: String,
    var dataPoint: Double
) {
    //lateinit var sensor: ToOne<Sensor>
}