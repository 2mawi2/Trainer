package com.mawistudios.features.workout

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mawistudios.app.model.Workout
import java.util.*

class WorkoutViewModel(
    private val workoutRepo: IWorkoutRepo
) : ViewModel() {
    val workouts: MutableLiveData<List<Workout>> by lazy { MutableLiveData<List<Workout>>() }

    init {
        updateLiveData()
    }

    fun updateLiveData() {
        workouts.value = workoutRepo.all().sortedBy { it.createdDate }
    }

    fun addWorkoutPlaceholder() {
        addWorkout(Workout(
            name = "Workout ${(workouts.value?.count() ?: 0) + 1}",
            createdDate = Calendar.getInstance().time
        ))
    }

    fun addWorkout(workoutPlaceholder: Workout) {
        workoutRepo.save(workoutPlaceholder)
        updateLiveData()
    }

    fun removeWorkout(workout: Workout) {
        workoutRepo.remove(workout.id)
        updateLiveData()
    }
}