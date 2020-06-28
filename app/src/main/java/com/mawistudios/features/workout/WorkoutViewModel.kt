package com.mawistudios.features.workout

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mawistudios.app.model.Workout
import com.mawistudios.data.local.IWorkoutRepo

class WorkoutViewModel(
    private val workoutRepo: IWorkoutRepo
) : ViewModel() {
    val workouts: MutableLiveData<List<Workout>> by lazy { MutableLiveData<List<Workout>>() }

    init {
        updateWorkouts()
    }

    fun updateWorkouts() {
        workouts.value = workoutRepo.getWorkouts()
    }
}