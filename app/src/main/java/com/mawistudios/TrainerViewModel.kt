package com.mawistudios

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import com.mawistudios.app.toGraphFormat
import com.mawistudios.data.local.*
import com.wahoofitness.connector.conn.connections.params.ConnectionParams
import java.time.Duration
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

data class DashboardData(
    var hearthRate: SensorData?,
    val bpm: String?,
    val kmh: String?,
    val cadence: String?
)

class TrainerViewModel(
    private val sessionRepo: ISessionRepo,
    private val sensorDataRepo: ISensorDataRepo
) : ViewModel() {
    private var session: Session
    private var hearthRateZones: List<Zone>
    var trainingProgram: TrainingProgram

    init {
        val id = sessionRepo.add(Session())
        session = sessionRepo.get(id)
        hearthRateZones = getUserHearthRateZones()
        trainingProgram = pullTrainingProgram()
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


    private fun pullTrainingProgram(): TrainingProgram {
        val targetCadence = Zone(70.0, 80.0)

        val intervals = (hearthRateZones.take(6) + hearthRateZones.take(6).reversed()).map {
            TrainingInterval(
                duration = Duration.ofSeconds(60).toMillis(),
                targetCadence = targetCadence,
                targetHearthRate = it
            )
        }

        return TrainingProgram(intervals = intervals)
    }


    fun trainingProgramSets(): List<ArrayList<Entry>> {
        return pullTrainingProgram()
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
        val currentDuration = currentDuration(hearthRateData.time).seconds
        return currentDuration >= intervalStart
    }

    fun currentHearthRate(): SensorData? {
        return sensorDataRepo.currentHearthRate()
    }

    fun maybeUpdateStartTime(hearthRateData: SensorData?) {
        hearthRateData?.let { hrData ->
            if (!session.hasStarted()) {
                session = sessionRepo.update(session.id) { s ->
                    s.startTime = hrData.time
                }
            }
        }
    }


    fun currentSpeed(): SensorData? {
        return sensorDataRepo.currentSpeed()
    }

    fun currentCadence(): SensorData? {
        return sensorDataRepo.currentCadence()
    }

    fun currentDistance(): SensorData? {
        return sensorDataRepo.currentDistance()
    }

    fun hasSessionStarted(): Boolean {
        return session.hasStarted()
    }

    //calculates passed session interval until now
    fun getPassedInterval(hearthRateData: SensorData): ArrayList<Entry> {
        val passedIntervals = pullTrainingProgram()
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

    val targetHearthRate: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val targetCadence: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    fun updateTargetValues(hearthRateData: SensorData?) {
        val currentInterval = getCurrentInterval(hearthRateData)
        targetHearthRate.value = currentInterval.targetHearthRate.toString()
        targetCadence.value = currentInterval.targetCadence.toString()
    }

    fun getCurrentInterval(hearthRateData: SensorData?): TrainingInterval {
        if (hearthRateData == null || session.startTime == null) {
            return trainingProgram.intervals.first()
        }

        val currentDuration = hearthRateData.time.time - session.startTime!!.time

        val start = Duration.ZERO
        return trainingProgram.intervals.first { interval ->
            val end = start.plusMillis(interval.duration)
            return@first currentDuration >= start.toMillis() && currentDuration < end.toMillis()
        }
    }


    val dashboardData: MutableLiveData<DashboardData> by lazy {
        MutableLiveData<DashboardData>()
    }

    private val trainingSessionObserver = object : ITrainingSessionObserver {
        override fun onTrainingDataChanged() {
            val hearthRateData = currentHearthRate()
            dashboardData.value = DashboardData(
                hearthRate = hearthRateData,
                bpm = hearthRateData?.dataPoint?.roundToInt()?.toString() ?: "-",
                kmh = currentSpeed()?.dataPoint?.roundToInt()?.toString() ?: "-",
                cadence = currentCadence()?.dataPoint?.roundToInt()?.toString() ?: "-"
            )
            maybeUpdateStartTime(hearthRateData)
            updateTargetValues(hearthRateData)
        }

        override fun onDiscoveryStarted() {}
        override fun onSensorConnectionStateChanged(
            deviceName: String,
            state: String
        ) {}
    }

    fun onStart() {
        TrainingSessionObservable.register(trainingSessionObserver)
    }

    fun onStop() {
        TrainingSessionObservable.unRegister(trainingSessionObserver)
    }
}