package com.mawistudios.app

import com.mawistudios.app.model.Workout
import com.mawistudios.app.model.Zone
import java.time.Duration
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


fun Workout.toGraphFormat(): List<List<Pair<Float, Float>>> {
    var totalDuration: Duration = Duration.ZERO
    val graphDataPoints: MutableList<List<Pair<Float, Float>>> = mutableListOf()
    intervals.indices.map { i ->
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