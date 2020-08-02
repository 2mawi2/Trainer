package com.mawistudios.features.workout.detail

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mawistudios.features.interval.detail.IntervalAdapter
import com.mawistudios.features.workout.detail.interval.IntervalDetailActivity
import com.mawistudios.features.workout.WorkoutActivity
import com.mawistudios.trainer.R
import kotlinx.android.synthetic.main.activity_workout_detail.*
import org.koin.android.ext.android.inject


class WorkoutDetailActivity : AppCompatActivity() {
    private val viewModel: WorkoutDetailViewModel by inject()
    private lateinit var intervalAdapter: IntervalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUIComponents()
        intent.extras?.getLong("workoutId")?.let { workoutId ->
            viewModel.setWorkout(workoutId)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, WorkoutActivity::class.java))
    }


    private fun setupUIComponents() {
        setContentView(R.layout.activity_workout_detail)

        setupIntervalUICompontents()
        setupWorkoutUICompontents()
    }

    private fun setupWorkoutUICompontents() {
        viewModel.workout.observe(this, Observer {
            workout_name_input.setText(it.name)
        })
        update_workout_btn.setOnClickListener {
            viewModel.setWorkoutName(workout_name_input.text.toString())
            viewModel.saveWorkout()
            startActivity(Intent(this, WorkoutActivity::class.java))
        }
        cancel_btn.setOnClickListener {
            startActivity(Intent(this, WorkoutActivity::class.java))
        }
    }

    private fun setupIntervalUICompontents() {
        viewModel.intervals.observe(this, Observer { intervals ->
            intervalAdapter.submitList(intervals)
        })
        if (!::intervalAdapter.isInitialized) {
            intervalAdapter = IntervalAdapter(
                onClickRemove = { interval ->
                    viewModel.removeInterval(interval)
                },
                onClickModify = { interval ->
                    val intent = Intent(this, IntervalDetailActivity::class.java)
                    intent.putExtra("intervalId", interval.id)
                    intent.putExtra("workoutId", viewModel.workout.value?.id)
                    startActivity(intent)
                }
            )
        }
        list_intervals.adapter = intervalAdapter
        add_interval_btn.setOnClickListener { viewModel.addIntervalPlaceholder() }
        add_loop_btn.setOnClickListener { viewModel.addLoopPlaceholder() }
    }
}