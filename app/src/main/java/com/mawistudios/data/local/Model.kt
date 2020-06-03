package com.mawistudios.data.local

import android.graphics.Color
import com.mawistudios.app.sumByLong
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.time.Duration
import java.util.*
import kotlin.math.abs


@Entity
data class Sensor(
    @Id var id: Long = 0,
    var state: String? = null,
    var name: String
) {
    fun isConnected(): Boolean = this.state.equals("connected", true)
}

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

@Entity
data class Session(
    @Id var id: Long = 0,
    var startTime: Date? = null,
    var endTime: Date? = null
) {
    fun hasStarted() = startTime != null
    fun hasEnded() = endTime != null
}

fun areClose(first: Double, second: Double, precision: Double): Boolean {
    val distance = abs(first - second)
    return distance <= precision
}

data class Zone(
    var min: Double,
    var max: Double,
    var name: String = "",
    var index: Int = 0
) {
    fun matches(hearthRate: Double): Boolean {
        val isMin = areClose(hearthRate, min, precision = 0.0001)
        val isMax = areClose(hearthRate, max, precision = 0.0001)

        if (hearthRate == 0.0 && isMin) {
            return true
        }
        return hearthRate > min && (hearthRate < max || isMax)
    }

    override fun toString(): String {
        return "${min.toInt()}-${max.toInt()}"
    }
}

data class TrainingInterval(
    var duration: Long,
    var targetCadence: Zone,
    var targetHearthRate: Zone
)

data class TrainingProgram(
    var intervals: List<TrainingInterval>
) {
    fun totalDuration(): Duration {
        return Duration.ofMillis(intervals.sumByLong { it.duration })
    }
}