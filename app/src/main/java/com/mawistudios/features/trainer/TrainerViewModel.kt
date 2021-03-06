package com.mawistudios.features.trainer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import com.mawistudios.data.hardware.ITrainingSessionObserver
import com.mawistudios.data.hardware.TrainingSessionObservable
import com.mawistudios.app.ILogger
import com.mawistudios.app.calcIntervalProgressPercentage
import com.mawistudios.app.model.*
import com.mawistudios.app.toGraphFormat
import com.mawistudios.data.local.*
import com.mawistudios.features.workout.IWorkoutRepo
import java.lang.Error
import java.time.Duration
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class TrainerViewModel(
    private val logger: ILogger,
    private val sessionRepo: ISessionRepo,
    private val athleteRepo: IAthleteRepo,
    private val workoutRepo: IWorkoutRepo,
    private val sensorDataRepo: ISensorDataRepo
) : ViewModel() {
    var session: Session
    var hearthRateZones: List<Zone>
    var workout: Workout

    val targetHearthRate: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val targetCadence: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val remainingInterval: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val dashboardData: MutableLiveData<DashboardData> by lazy {
        MutableLiveData<DashboardData>()
    }
    val hearthRateProgress: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val cadenceProgress: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }


    init {
        val recentlyActive = sessionRepo.getLastActiveOrNull()
        session = if (recentlyActive != null) recentlyActive else {
            val id = sessionRepo.save(Session())
            sessionRepo.get(id)
        }
        hearthRateZones = athleteRepo.getUserHearthRateZones()
        workout = workoutRepo.getWorkout()
    }


    fun trainingProgramSets(): List<ArrayList<Entry>> {
        return workout
            .toGraphFormat()
            .map { ArrayList(it.map { p -> Entry(p.first, p.second) }) }
    }

    fun currentDuration(currentTime: Date): Duration {
        return session.startTime?.let {
            Duration.ofMillis(currentTime.time - it.time)
        } ?: Duration.ZERO
    }

    fun isIntervalInThePast(
        hearthRateData: SensorData,
        it: ArrayList<Entry>
    ): Boolean {
        val intervalStart = it[0].x
        val currentDuration = currentDuration(hearthRateData.time).toMillis()
        return currentDuration >= intervalStart
    }

    fun currentHearthRate(): SensorData? {
        return sensorDataRepo.currentHearthRate()
    }

    fun updateStartTime(currentTime: Date) {
        if (!session.hasStarted()) {
            session = sessionRepo.update(session.id) { s ->
                s.startTime = currentTime
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
        val passedIntervals = workout
            .toGraphFormat()
            .map { ArrayList(it.map { p -> Entry(p.first, p.second) }) }
            .filter { isIntervalInThePast(hearthRateData, it) }

        val currentDuration = currentDuration(hearthRateData.time)

        passedIntervals.last()[1].x = currentDuration.toMillis().toFloat()
        passedIntervals.last()[2].x = currentDuration.toMillis().toFloat()
        passedIntervals.last()[2].y = passedIntervals.last()[1].y

        val userInterval = ArrayList(passedIntervals.flatMap { i -> i.map { j -> j } })

        return userInterval
    }


    fun updateTargetValues(currentTime: Date?) {
        val currentInterval = currentInterval(currentTime)
        targetHearthRate.value = currentInterval.targetHearthRate.toString()
        targetCadence.value = currentInterval.targetCadence.toString()
    }

    fun currentInterval(currentTime: Date?): Interval {
        var intervals = workout.intervals
        if (intervals == null) throw Error("no intervals")

        if (currentTime == null || session.startTime == null) {
            return intervals.first()
        }

        val currentMillis = currentDuration(currentTime).toMillis()
        logger.log("currentMillis: $currentMillis")
        logger.log("trainingProgram.intervals: ${intervals.map { "${it.start} ${it.end}" }}")

        return intervals.first {
            currentMillis >= it.start && currentMillis < it.end
        }
    }


    private fun updateRemainingInterval(currentTime: Date?) {
        val currentInterval = currentInterval(currentTime)
        if (currentTime == null) {
            remainingInterval.value = currentInterval.duration().seconds.toString()
        }

        val currentMillis = currentDuration(currentTime!!).toMillis()
        val remainingDuration = currentInterval.end - currentMillis
        remainingInterval.value = Duration.ofMillis(remainingDuration).seconds.toString()
    }


    private val trainingSessionObserver = object :
        ITrainingSessionObserver {
        override fun onTrainingDataChanged() {
            val hearthRateData = currentHearthRate()
            dashboardData.value = DashboardData(
                hearthRate = hearthRateData,
                bpm = hearthRateData?.dataPoint?.roundToInt()?.toString() ?: "-",
                kmh = currentSpeed()?.dataPoint?.roundToInt()?.toString() ?: "-",
                cadence = currentCadence()?.dataPoint?.roundToInt()?.toString() ?: "-"
            )

            hearthRateData?.let {
                val currentTime = hearthRateData.time
                updateStartTime(currentTime)
                updateTargetValues(currentTime)
                updateRemainingInterval(currentTime)
                updateHearthRateProgress(currentTime, hearthRateData.dataPoint)
                updateCadenceProgress(currentTime)
            }
        }

        override fun onDiscoveryStarted() {}
        override fun onSensorConnectionStateChanged(sensor: Sensor) {}
    }

    private fun updateHearthRateProgress(currentTime: Date, hearthRate: Double) {
        val interval = currentInterval(currentTime)
        val percentage = calcIntervalProgressPercentage(interval.targetHearthRate, hearthRate)
        hearthRateProgress.value = percentage
    }

    private fun updateCadenceProgress(currentTime: Date) {
        currentCadence()?.let { cadence ->
            val interval = currentInterval(currentTime)
            val percentage =
                calcIntervalProgressPercentage(interval.targetCadence, cadence.dataPoint)
            cadenceProgress.value = percentage
        }
    }

    fun onStart() {
        TrainingSessionObservable.register(
            trainingSessionObserver
        )
    }

    fun onStop() {
        TrainingSessionObservable.unRegister(
            trainingSessionObserver
        )
    }
}