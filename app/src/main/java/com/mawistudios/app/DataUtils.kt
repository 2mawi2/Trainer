package com.mawistudios.app

import com.github.mikephil.charting.data.Entry
import com.mawistudios.data.local.TrainingInterval
import com.mawistudios.data.local.TrainingProgram
import com.mawistudios.data.local.Zone
import java.time.Duration
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.roundToInt


fun TrainingProgram.toGraphFormat(): List<List<Pair<Float, Float>>> {
    var totalDuration: Duration = Duration.ZERO
    val graphDataPoints: MutableList<List<Pair<Float, Float>>> = mutableListOf()


    this.intervals.indices.map { i ->
        val intervalData: ArrayList<Pair<Float, Float>> = arrayListOf()
        intervalData.add(
            Pair(
                totalDuration.toMillis().toFloat(),
                intervals[i].targetHearthRate.max.toFloat()
            )
        )
        totalDuration = totalDuration.plusMillis(intervals[i].duration)
        intervalData.add(
            Pair(
                totalDuration.toMillis().toFloat(),
                intervals[i].targetHearthRate.max.toFloat()
            )
        )

        if (intervals.indices.contains(i + 1)) {
            intervalData.add(
                Pair(
                    totalDuration.toMillis().toFloat(),
                    intervals[i + 1].targetHearthRate.max.toFloat()
                )
            )
        }

        graphDataPoints.add(intervalData)
    }

    return graphDataPoints
}


fun calcIntervalProgressPercentage(
    targetZone: Zone,
    measurement: Double
): Int {
    var achieved = measurement - targetZone.min
    achieved = achieved.coerceIn(0.0, targetZone.delta)
    val progress = (achieved / targetZone.delta) * 100
    return progress.roundToInt()
}