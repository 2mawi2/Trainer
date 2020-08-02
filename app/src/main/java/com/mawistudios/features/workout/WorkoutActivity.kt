package com.mawistudios.features.workout

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mawistudios.MainActivity
import com.mawistudios.app.toast
import com.mawistudios.features.workout.detail.WorkoutDetailActivity
import com.mawistudios.trainer.R
import kotlinx.android.synthetic.main.activity_workout.*
import kotlinx.android.synthetic.main.activity_workout_detail.*
import org.koin.android.ext.android.inject


class WorkoutActivity : AppCompatActivity() {
    private val viewModel: WorkoutViewModel by inject()
    private lateinit var workoutAdapter: WorkoutAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUIComponents()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateLiveData()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun setupUIComponents() {
        setContentView(R.layout.activity_workout)

        viewModel.workouts.observe(this, Observer { workouts ->
            workoutAdapter.submitList(workouts)
        })


        if (!::workoutAdapter.isInitialized) {
            workoutAdapter = WorkoutAdapter(
                onClickRemove = {
                    viewModel.removeWorkout(it)
                    toast("Workout deleted!")
                },
                onClickModify = { workout ->
                    val intent = Intent(this, WorkoutDetailActivity::class.java)
                    intent.putExtra("workoutId", workout.id)
                    startActivity(intent)
                }
            )
        }
        list_workouts.adapter = workoutAdapter

        add_workout_btn.setOnClickListener {
            viewModel.addWorkoutPlaceholder()
        }
    }
}