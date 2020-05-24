package com.mawistudios.app

import com.github.mikephil.charting.data.Entry
import com.mawistudios.data.local.TrainingInterval
import com.mawistudios.data.local.TrainingProgram
import java.time.Duration


fun TrainingProgram.toGraphFormat(): List<List<Pair<Float, Float>>> {
    var totalDuration: Duration = Duration.ZERO
    val graphDataPoints: MutableList<List<Pair<Float, Float>>> = mutableListOf()

    this.intervals.forEach {
        val intervalData: ArrayList<Pair<Float, Float>> = arrayListOf()
        intervalData.add(
            Pair(
                totalDuration.toMinutes().toFloat(),
                it.targetHearthRate.max.toFloat()
            )
        )
        totalDuration = totalDuration.plusMillis(it.duration)
        intervalData.add(
            Pair(
                totalDuration.toMinutes().toFloat(),
                it.targetHearthRate.max.toFloat()
            )
        )
        graphDataPoints.add(intervalData)
    }

    return graphDataPoints
}