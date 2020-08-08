package com.mawistudios.features.workout.detail.interval

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.ikovac.timepickerwithseconds.MyTimePickerDialog
import com.mawistudios.app.toast
import com.mawistudios.features.workout.detail.WorkoutDetailActivity
import com.mawistudios.trainer.R
import kotlinx.android.synthetic.main.activity_interval_detail.*
import org.koin.android.ext.android.inject


class IntervalDetailActivity : AppCompatActivity() {
    private val viewModel: IntervalDetailViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUIComponents()
        subscribeToUiComponents()

        intent.extras?.getLong("workoutId")?.let { workoutId ->
            viewModel.workoutId = workoutId
        }
        intent.extras?.getLong("intervalId")?.let { intervalId ->
            viewModel.setInterval(intervalId)
        }
    }

    private fun subscribeToUiComponents() {
        viewModel.interval.observe(this, Observer {
            interval_name_input.setText(it.name)
            select_duration_value.text = it.getFormattedDuration()
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToWorkoutActivity()
    }

    private fun navigateToWorkoutActivity() {
        val intent = Intent(this, WorkoutDetailActivity::class.java)
        intent.putExtra("workoutId", viewModel.workoutId)
        startActivity(intent)
    }

    private fun setupUIComponents() {
        setContentView(R.layout.activity_interval_detail)
        update_interval_btn.setOnClickListener {
            viewModel.setIntervalName(interval_name_input.text.toString())
            viewModel.saveInterval()
            navigateToWorkoutActivity()
        }
        cancel_btn.setOnClickListener { navigateToWorkoutActivity() }
        setupDurationPicker()
        setupSelectPowerSpinner()
    }

    private fun setupDurationPicker() {
        select_duration_dialog.setOnClickListener {
            val timePickerDialog = getTimePickerDialog()
            timePickerDialog.show()
        }
    }

    private fun setupSelectPowerSpinner() {
        val zones = viewModel.getUserPowerZones()
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            zones.map { "${it.index}: ${it.name}" }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        select_power_spinner.adapter = adapter
        select_power_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                toast("selected: ${zones[position].name}")
            }
        }
    }

    private fun getTimePickerDialog() = MyTimePickerDialog(
        this,
        MyTimePickerDialog.OnTimeSetListener { view, hours, minutes, seconds ->
            viewModel.setIntervalDuration(hours, minutes, seconds)
        },
        viewModel.interval.value!!.hours,
        viewModel.interval.value!!.minutes,
        viewModel.interval.value!!.seconds,
        true
    )

}