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
import com.mawistudios.data.local.SensorDataRepo
import com.mawistudios.trainer.R
import kotlinx.coroutines.*
import kotlin.math.roundToInt

var entries = arrayListOf(
    Entry(1.0f, 50.0f),
    Entry(2.0f, 50.0f),
    Entry(2.0f, 100.0f),
    Entry(3.0f, 100.0f)
)

var entries2 = arrayListOf(
    Entry(3.0f, 100.0f),
    Entry(4.0f, 100.0f),
    Entry(4.0f, 30.0f),
    Entry(5.0f, 30.0f)
)


class TrainerActivity : AppCompatActivity() {
    private lateinit var trainingChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        setContentView(R.layout.activity_trainer)

        val set = buildSet(entries, alpha = 90)
        val set2 = buildSet(entries2, alpha = 10)

        this.trainingChart = findViewById<LineChart>(R.id.training_chart).apply {
            legend.isEnabled = false
            xAxis.isEnabled = false
            getAxis(YAxis.AxisDependency.LEFT).textSize = 15f
            getAxis(YAxis.AxisDependency.RIGHT).isEnabled = false
            description.isEnabled = false
            setPinchZoom(false)
            setTouchEnabled(false)
            data = LineData(set, set2)
        }
    }

    private val trainingSessionObserver = object : ITrainingSessionObserver {
        override fun onTrainingDataChanged() {
            val bpm = SensorDataRepo.currentHearthRate()?.dataPoint?.roundToInt()?.toString() ?: "-"
            val kmh = SensorDataRepo.currentSpeed()?.dataPoint?.roundToInt()?.toString() ?: "-"
            val cadence = SensorDataRepo.currentCadence()?.dataPoint?.roundToInt()?.toString() ?: "-"
            //val currentDistance = SensorDataRepo.currentDistance()

            GlobalScope.launch(Dispatchers.Main) {
                findViewById<TextView>(R.id.hr_text).text = bpm
                findViewById<TextView>(R.id.speed_text).text = kmh
                findViewById<TextView>(R.id.rpm_text).text = cadence
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

    private fun buildSet(entries2: ArrayList<Entry>, alpha: Int): LineDataSet {
        return LineDataSet(entries2, "Training Data").apply {
            setDrawIcons(false)
            color = Color.DKGRAY
            setCircleColor(Color.DKGRAY)
            lineWidth = 4f
            circleRadius = 1f
            setDrawCircleHole(false)
            setDrawFilled(true)
            formLineWidth = 1f
            fillColor = Color.BLUE
            fillAlpha = alpha
            setDrawValues(false)
        }
    }

}
