package com.mawistudios.features.workout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mawistudios.app.model.Workout
import com.mawistudios.trainer.R
import kotlinx.android.synthetic.main.activity_workout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class WorkoutActivity : AppCompatActivity() {
    private val viewModel: WorkoutViewModel by inject()
    private lateinit var workoutAdapter: WorkoutAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUIComponents()
    }

    private fun setupUIComponents() {
        setContentView(R.layout.activity_workout)
        viewModel.workouts.observe(this, Observer { workouts ->
            workoutAdapter.submitList(workouts)
        })
        if (!::workoutAdapter.isInitialized) {
            workoutAdapter = WorkoutAdapter()
        }
        list_workouts.adapter = workoutAdapter
    }
}