package com.mawistudios

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.mawistudios.data.local.*
import com.mawistudios.trainer.R
import kotlinx.coroutines.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class TrainerActivity : AppCompatActivity() {
    private lateinit var trainingChart: LineChart
    private lateinit var viewModel: TrainerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = TrainerViewModel()
        setupUI()
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
            color = Color.DKGRAY
            lineWidth = 2f
            setDrawCircleHole(false)
            setDrawCircles(false)
            setDrawFilled(true)
            formLineWidth = 1f
            fillColor = col
            fillAlpha = 200
            setDrawValues(false)
        }
    }

    private fun Zone.color(): Int = when (this.index) {
        0 -> Color.BLACK
        1 -> getColor(R.color.hr1)
        2 -> getColor(R.color.hr2)
        3 -> getColor(R.color.hr3)
        4 -> getColor(R.color.hr4)
        5 -> getColor(R.color.hr5)
        6 -> getColor(R.color.hr6)
        7 -> getColor(R.color.hr7)
        else -> Color.BLACK
    }

    private fun trainingProgramChartSet(): List<LineDataSet> {
        return viewModel.trainingProgramSets().map {
            val userHearthRate = viewModel.getUserHearthRateZones().first { z ->
                z.matches(it.first().y.toDouble())
            }
            return@map buildSet(it, alpha = 90, col = userHearthRate.color())
        }
    }

    private fun setupGraph() {
        val sets = trainingProgramChartSet()

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

    private val trainingSessionObserver = object : ITrainingSessionObserver {
        override fun onTrainingDataChanged() {
            var session = viewModel.getSession()
            val hearthRateData = viewModel.currentHearthRate()
            val bpm = hearthRateData?.dataPoint?.roundToInt()?.toString() ?: "-"
            val kmh = viewModel.currentSpeed()?.dataPoint?.roundToInt()?.toString() ?: "-"
            val cadence = viewModel.currentCadence()?.dataPoint?.roundToInt()?.toString() ?: "-"
            //val currentDistance = viewModel.currentDistance()

            viewModel.maybeUpdateStartTime(hearthRateData)

            GlobalScope.launch(Dispatchers.Main) {
                findViewById<TextView>(R.id.hr_text).text = bpm
                findViewById<TextView>(R.id.speed_text).text = kmh
                findViewById<TextView>(R.id.rpm_text).text = cadence
            }

            if (viewModel.hasSessionStarted()) {
                hearthRateData?.let {
                    var userInterval = viewModel.getPassedInterval(hearthRateData)

                    val userSet = buildSet(userInterval, alpha = 256, col = Color.BLACK).apply {
                        isHighlightEnabled = true
                        setDrawHighlightIndicators(true)
                        highLightColor = Color.BLACK
                        highlightLineWidth = 4f
                    }

                    val sets = trainingProgramChartSet().toMutableList()
                    sets.add(userSet)

                    this@TrainerActivity.trainingChart.data = LineData(sets.toList())
                    this@TrainerActivity.trainingChart.highlightValue(
                        viewModel.currentDuration(hearthRateData.time).seconds.toFloat(),
                        sets.indices.last
                    )
                    this@TrainerActivity.trainingChart.notifyDataSetChanged()
                    this@TrainerActivity.trainingChart.invalidate()
                }
            }
        }

        override fun onDiscoveryStarted() {

        }

        override fun onSensorConnectionStateChanged(deviceName: String, state: String) {

        }
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
