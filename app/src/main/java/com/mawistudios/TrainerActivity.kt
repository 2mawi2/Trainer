package com.mawistudios

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.mawistudios.data.local.*
import com.mawistudios.trainer.R
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import kotlin.collections.ArrayList


class TrainerActivity : AppCompatActivity() {
    private val viewModel: TrainerViewModel by inject()

    private lateinit var trainingChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        viewModel.dashboardData.observe(this, dashboardDataObserver)
        viewModel.targetCadence.observe(this, Observer {
            GlobalScope.launch(Dispatchers.Main) {
                findViewById<TextView>(R.id.target_rpm_text).text = it
            }
        })
        viewModel.targetHearthRate.observe(this, Observer {
            GlobalScope.launch(Dispatchers.Main) {
                findViewById<TextView>(R.id.target_hr_text).text = it
            }
        })
        setContentView(R.layout.activity_trainer)
        setupGraph()
        findViewById<GraphView>(R.id.graph).let {
            it.addSeries(
                LineGraphSeries(
                    arrayOf(
                        DataPoint(0.0, 1.0),
                        DataPoint(1.0, 5.0),
                        DataPoint(2.0, 3.0),
                        DataPoint(3.0, 2.0),
                        DataPoint(4.0, 6.0)
                    )
                )
            )
        }

    }

    private val dashboardDataObserver = Observer<DashboardData> {
        GlobalScope.launch(Dispatchers.Main) {
            findViewById<TextView>(R.id.hr_text).text = it.bpm
            findViewById<TextView>(R.id.speed_text).text = it.kmh
            findViewById<TextView>(R.id.rpm_text).text = it.cadence
        }

        if (viewModel.hasSessionStarted()) {
            it.hearthRate?.let { hr ->
                updateIntervalChart(hr)
            }
        }
    }

    private fun updateIntervalChart(hr: SensorData) {
        val userInterval = viewModel.getPassedInterval(hr)

        val sets = trainingProgramChartSet().toMutableList().apply {
            add(buildSet(userInterval, alpha = 256, col = Color.BLACK).apply {
                isHighlightEnabled = true
                setDrawHighlightIndicators(true)
                highLightColor = Color.BLACK
                highlightLineWidth = 4f
            })
        }

        trainingChart = trainingChart.apply {
            data = LineData(sets.toList())
            highlightValue(
                viewModel.currentDuration(hr.time).seconds.toFloat(),
                sets.indices.last
            )
            notifyDataSetChanged()
            invalidate()
        }
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

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }
}
