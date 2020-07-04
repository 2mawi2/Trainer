package com.mawistudios.features.workout.detail

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mawistudios.MainActivity
import com.mawistudios.app.toast
import com.mawistudios.features.workout.WorkoutActivity
import com.mawistudios.trainer.R
import kotlinx.android.synthetic.main.activity_workout_detail.*
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

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, WorkoutActivity::class.java))
    }


    private fun setupUIComponents() {
        setContentView(R.layout.activity_workout_detail)
        viewModel.workout.observe(this, Observer {
            workout_name_input.setText(it.name)
        })
        update_workout_btn.setOnClickListener {
            viewModel.saveForm(
                formName = workout_name_input.text.toString()
            )
            toast(this, "Workout updated!")
            startActivity(Intent(this, WorkoutActivity::class.java))
        }
        cancel_btn.setOnClickListener {
            startActivity(Intent(this, WorkoutActivity::class.java))
        }
    }
}