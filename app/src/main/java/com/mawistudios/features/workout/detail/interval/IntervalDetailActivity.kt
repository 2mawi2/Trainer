package com.mawistudios.features.workout.detail.interval

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mawistudios.features.workout.detail.WorkoutDetailActivity
import com.mawistudios.trainer.R
import kotlinx.android.synthetic.main.activity_interval_detail.*
import kotlinx.android.synthetic.main.activity_interval_detail.cancel_btn
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
    }
}