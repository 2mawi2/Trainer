package com.mawistudios.features.workout.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mawistudios.app.model.Workout
import com.mawistudios.trainer.R
import kotlinx.android.synthetic.main.list_item_workout.*
import org.koin.android.ext.android.inject


class WorkoutDetailActivity : AppCompatActivity() {
    private val viewModel: WorkoutDetailViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUIComponents()
        intent.extras?.getLong("workoutId")?.let { workoutId ->
            viewModel.setWorkout(workoutId)
        }
    }

    private fun setupUIComponents() {
        setContentView(R.layout.activity_workout_detail)
        viewModel.workout.observe(this, Observer {
            workout_name.text = it.name
            workout_name.text = it.formatedCreatedDate
        })
    }
}