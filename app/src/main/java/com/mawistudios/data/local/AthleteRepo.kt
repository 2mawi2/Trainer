package com.mawistudios.data.local

import com.mawistudios.app.log
import com.mawistudios.app.model.TrainingInterval
import com.mawistudios.app.model.TrainingProgram
import com.mawistudios.app.model.Zone
import java.time.Duration






interface IAthleteRepo {
    fun getUserHearthRateZones(): List<Zone>
    fun getTrainingProgram(): TrainingProgram
}

class AthleteRepo : IAthleteRepo {
    override fun getTrainingProgram(): TrainingProgram {
        val targetCadence = Zone(70.0, 80.0)

        var intervalStart: Long = 0
        val hearthRateZones = getUserHearthRateZones()
        val intervals =
            (hearthRateZones.take(6) + hearthRateZones.take(6).reversed()).toList().map {
                val duration = Duration.ofSeconds(60).toMillis()
                val intervalEnd = intervalStart + duration
                val interval = TrainingInterval(
                    start = intervalStart,
                    end = intervalEnd,
                    duration = duration,
                    targetCadence = targetCadence,
                    targetHearthRate = it
                )
                intervalStart = intervalEnd
                interval
            }
        intervals.forEach { log(it.toString()) }
        return TrainingProgram(intervals = intervals)
    }

    override fun getUserHearthRateZones(): List<Zone> {
        val threshold = 164.0
        return listOf(
            Zone(
                0.0 * threshold,
                0.81 * threshold,
                "1 Recovery",
                index = 1
            ),
            Zone(
                0.81 * threshold,
                0.89 * threshold,
                "2 Aerobic",
                index = 2
            ),
            Zone(
                0.89 * threshold,
                0.93 * threshold,
                "3 Tempo",
                index = 3
            ),
            Zone(
                0.93 * threshold,
                0.99 * threshold,
                "4 SubThreshold",
                index = 4
            ),
            Zone(
                0.99 * threshold,
                1.02 * threshold,
                "5A SubThreshold",
                index = 5
            ),
            Zone(
                1.02 * threshold,
                1.06 * threshold,
                "5B Aerobic Capacity",
                index = 6
            ),
            Zone(
                1.06 * threshold,
                1.3 * threshold,
                "5C Anaerobic Capacity",
                index = 7
            )
        )
    }
}