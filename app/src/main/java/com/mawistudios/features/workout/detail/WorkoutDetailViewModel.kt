package com.mawistudios.features.workout.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mawistudios.app.model.Workout
import com.mawistudios.data.local.IWorkoutRepo
import java.util.*

class WorkoutDetailViewModel(
    private val workoutRepo: IWorkoutRepo
) : ViewModel() {
    val workout: MutableLiveData<Workout> by lazy { MutableLiveData<Workout>() }

    fun setWorkout(workoutId: Long) {
        workout.value = workoutRepo.get(workoutId)
    }

    fun updateWorkout() {
        workout.value = workout.value?.id?.let { workoutRepo.get(it) }
    }

}