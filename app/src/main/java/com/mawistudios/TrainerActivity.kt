package com.mawistudios

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.mawistudios.app.log
import com.mawistudios.app.toGraphFormat
import com.mawistudios.data.local.*
import com.mawistudios.trainer.R
import kotlinx.coroutines.*
import java.time.Duration
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class TrainerActivity : AppCompatActivity() {
    private lateinit var trainingChart: LineChart
    private lateinit var trainingProgram: TrainingProgram
    private lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trainingProgram = getTrainingProgram()
        setupUI()
        var newSessionId = SessionRepo.add(Session())
        session = SessionRepo.get(newSessionId)
    }

    private fun getTrainingProgram(): TrainingProgram {
        val zones = getUserHearthRateZones()
        val targetCadence = Zone(70.0, 80.0)

        val intervals = zones.take(6).map {
            TrainingInterval(
                duration = Duration.ofMinutes(10).toMillis(),
                targetCadence = targetCadence,
                targetHearthRate = it
            )
        }

        return TrainingProgram(intervals = intervals)
    }

    private fun setupUI() {
        setContentView(R.layout.activity_trainer)
        setupGraph()
    }


    private fun buildSet(
        entries: ArrayList<Entry>,
        alpha: Int,
        col: Int = Color.BLUE
    ): LineDataSet {
        return LineDataSet(entries, "Training Data").apply {
            setDrawIcons(false)
            color = Color.WHITE
            lineWidth = 2f
            circleRadius = 1f
            setCircleColor(Color.WHITE)
            setDrawCircleHole(false)
            setDrawFilled(true)
            formLineWidth = 1f
            form
            fillColor = col
            fillAlpha = alpha
            setDrawValues(false)
        }
    }

    private fun getUserHearthRateZones(): List<Zone> {
        val threshold = 164.0
        return listOf(
            Zone(0.0 * threshold, 0.81 * threshold, "1 Recovery", color = R.color.hr1),
            Zone(0.81 * threshold, 0.89 * threshold, "2 Aerobic", color = R.color.hr2),
            Zone(0.89 * threshold, 0.93 * threshold, "3 Tempo", color = R.color.hr3),
            Zone(0.93 * threshold, 0.99 * threshold, "4 SubThreshold", color = R.color.hr4),
            Zone(0.99 * threshold, 1.02 * threshold, "5A SubThreshold", color = R.color.hr5),
            Zone(1.02 * threshold, 1.06 * threshold, "5B Aerobic Capacity", color = R.color.hr6),
            Zone(1.06 * threshold, 1.3 * threshold, "5C Anaerobic Capacity", color = R.color.hr7)
        )
    }


    private fun setupGraph() {
        val sets = trainingProgramSets()
        this.trainingChart = findViewById<LineChart>(R.id.training_chart).apply {
            legend.isEnabled = false
            xAxis.isEnabled = false
            getAxis(YAxis.AxisDependency.LEFT).textSize = 15f
            getAxis(YAxis.AxisDependency.RIGHT).isEnabled = false
            description.isEnabled = false
            setPinchZoom(false)
            setTouchEnabled(false)
            data = LineData(sets)
        }
    }

    private fun trainingProgramSets(): List<LineDataSet> {
        return trainingProgram
            .toGraphFormat()
            .map { ArrayList(it.map { p -> Entry(p.first, p.second) }) }
            .map {
                val userHearthRate = getUserHearthRateZones().first { z ->
                    z.matches(it.first().y.toDouble())
                }
                log(userHearthRate.name)
                return@map buildSet(it, alpha = 90, col = userHearthRate.color)
            }
    }

    private val trainingSessionObserver = object : ITrainingSessionObserver {
        override fun onTrainingDataChanged() {
            val hearthRateData = SensorDataRepo.currentHearthRate()

            hearthRateData?.let { hrData ->
                if (!session.hasStarted()) {
                    session = SessionRepo.update(session.id) { s ->
                        s.startTime = hrData.time
                    }
                }
            }

            val bpm = hearthRateData?.dataPoint?.roundToInt()?.toString() ?: "-"
            val kmh = SensorDataRepo.currentSpeed()?.dataPoint?.roundToInt()?.toString() ?: "-"
            val cadence =
                SensorDataRepo.currentCadence()?.dataPoint?.roundToInt()?.toString() ?: "-"
            //val currentDistance = SensorDataRepo.currentDistance()

            GlobalScope.launch(Dispatchers.Main) {
                findViewById<TextView>(R.id.hr_text).text = bpm
                findViewById<TextView>(R.id.speed_text).text = kmh
                findViewById<TextView>(R.id.rpm_text).text = cadence
            }

            if (session.hasStarted()) {
                hearthRateData?.let {
                    val passedIntervals = trainingProgram
                        .toGraphFormat()
                        .map { ArrayList(it.map { p -> Entry(p.first, p.second) }) }
                        .filter { isIntervalInThePast(hearthRateData, it) }

                    var currentDuration = currentDuration(hearthRateData.time).toMinutes()
                    currentDuration = 25
                    passedIntervals.last()[1].x = currentDuration.toFloat()

                    val userInterval = ArrayList(passedIntervals.flatMap { i -> i.map { j -> j } })
                    val userSet = buildSet(userInterval, alpha = 90, col = Color.RED)

                    val sets = trainingProgramSets().toMutableList()
                    sets.add(userSet)

                    this@TrainerActivity.trainingChart.data = LineData(sets.toList())

                    this@TrainerActivity.trainingChart.invalidate()
                }
            }
        }

        override fun onDiscoveryStarted() {

        }

        override fun onSensorConnectionStateChanged(deviceName: String, state: String) {

        }
    }


    private fun currentDuration(currentTime: Date): Duration {
        return Duration.ofMillis(currentTime.time - session.startTime!!.time)
    }

    private fun isIntervalInThePast(
        hearthRateData: SensorData,
        it: ArrayList<Entry>
    ): Boolean {
        val intervalStart = it[0].x
        var currentDuration = currentDuration(hearthRateData.time).toMinutes()
        currentDuration = 25 // TODO remove
        return currentDuration >= intervalStart
    }

    override fun onStart() {
        super.onStart()
        TrainingSessionObservable.register(trainingSessionObserver)
    }

    override fun onStop() {
        super.onStop()
        TrainingSessionObservable.unRegister(trainingSessionObserver)
    }


}
