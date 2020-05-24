package com.mawistudios

import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import com.mawistudios.app.toGraphFormat
import com.mawistudios.data.local.*
import java.time.Duration
import java.util.*
import kotlin.collections.ArrayList

class TrainerViewModel : ViewModel() {
    private var session: Session

    init {
        session = SessionRepo.get(SessionRepo.add(Session()))
    }

    fun getUserHearthRateZones(): List<Zone> {
        val threshold = 164.0
        return listOf(
            Zone(0.0 * threshold, 0.81 * threshold, "1 Recovery", index = 1),
            Zone(0.81 * threshold, 0.89 * threshold, "2 Aerobic", index = 2),
            Zone(0.89 * threshold, 0.93 * threshold, "3 Tempo", index = 3),
            Zone(0.93 * threshold, 0.99 * threshold, "4 SubThreshold", index = 4),
            Zone(0.99 * threshold, 1.02 * threshold, "5A SubThreshold", index = 5),
            Zone(1.02 * threshold, 1.06 * threshold, "5B Aerobic Capacity", index = 6),
            Zone(1.06 * threshold, 1.3 * threshold, "5C Anaerobic Capacity", index = 7)
        )
    }

    fun getTrainingProgram(): TrainingProgram {
        val zones = getUserHearthRateZones()
        val targetCadence = Zone(70.0, 80.0)

        val intervals = (zones.take(6) + zones.take(6).reversed()).map {
            TrainingInterval(
                duration = Duration.ofSeconds(60).toMillis(),
                targetCadence = targetCadence,
                targetHearthRate = it
            )
        }

        return TrainingProgram(intervals = intervals)
    }


    fun trainingProgramSets(): List<ArrayList<Entry>> {
        return getTrainingProgram()
            .toGraphFormat()
            .map { ArrayList(it.map { p -> Entry(p.first, p.second) }) }
    }

    fun currentDuration(currentTime: Date): Duration {
        return Duration.ofMillis(currentTime.time - session.startTime!!.time)
    }

    fun isIntervalInThePast(
        hearthRateData: SensorData,
        it: ArrayList<Entry>
    ): Boolean {
        val intervalStart = it[0].x
        var currentDuration = currentDuration(hearthRateData.time).seconds
        return currentDuration >= intervalStart
    }

    fun currentHearthRate(): SensorData? {
        return SensorDataRepo.currentHearthRate()
    }

    fun maybeUpdateStartTime(hearthRateData: SensorData?) {
        hearthRateData?.let { hrData ->
            if (!session.hasStarted()) {
                session = SessionRepo.update(session.id) { s ->
                    s.startTime = hrData.time
                }
            }
        }
    }

    fun currentSpeed(): SensorData? {
        return SensorDataRepo.currentSpeed()
    }

    fun currentCadence(): SensorData? {
        return SensorDataRepo.currentCadence()
    }

    fun currentDistance(): SensorData? {
        return SensorDataRepo.currentDistance()
    }

    fun hasSessionStarted(): Boolean {
        return session.hasStarted()
    }

    //calculates passed session interval until now
    fun getPassedInterval(hearthRateData: SensorData): ArrayList<Entry> {
        val passedIntervals = getTrainingProgram()
            .toGraphFormat()
            .map { ArrayList(it.map { p -> Entry(p.first, p.second) }) }
            .filter { isIntervalInThePast(hearthRateData, it) }

        val currentDuration = currentDuration(hearthRateData.time).seconds

        passedIntervals.last()[1].x = currentDuration.toFloat()
        passedIntervals.last()[2].x = currentDuration.toFloat()
        passedIntervals.last()[2].y = passedIntervals.last()[1].y

        val userInterval = ArrayList(passedIntervals.flatMap { i -> i.map { j -> j } })

        return userInterval
    }

    fun getSession(): Session {
        return session
    }
}