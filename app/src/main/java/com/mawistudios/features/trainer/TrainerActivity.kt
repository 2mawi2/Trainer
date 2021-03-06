package com.mawistudios.features.trainer

import android.graphics.Color
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.mawistudios.app.model.SensorData
import com.mawistudios.app.model.Zone
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
        viewModel.remainingInterval.observe(this, Observer {
            GlobalScope.launch(Dispatchers.Main) {
                findViewById<TextView>(R.id.interval_text).text = it
            }
        })
        viewModel.hearthRateProgress.observe(this, Observer {
            GlobalScope.launch(Dispatchers.Main) {
                findViewById<ProgressBar>(R.id.hr_progress_bar).progress = it
                findViewById<TextView>(R.id.hr_text).setTextColor(
                    if (it > 95 || it < 5) Color.RED else Color.BLACK
                )
            }
        })
        viewModel.cadenceProgress.observe(this, Observer {
            GlobalScope.launch(Dispatchers.Main) {
                findViewById<ProgressBar>(R.id.rpm_progress_bar).progress = it
                findViewById<TextView>(R.id.rpm_text).setTextColor(
                    if (it > 95 || it < 5) Color.RED else Color.BLACK
                )
            }
        })
        setContentView(R.layout.activity_trainer)
        setupGraph()
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
                viewModel.currentDuration(hr.time).toMillis().toFloat(),
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
            val userHearthRate = viewModel.hearthRateZones.first { z ->
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
