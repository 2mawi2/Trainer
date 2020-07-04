package com.mawistudios.features.workout

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mawistudios.app.model.Workout
import com.mawistudios.data.local.IWorkoutRepo
import java.util.*

class WorkoutViewModel(
    private val workoutRepo: IWorkoutRepo
) : ViewModel() {
    val workouts: MutableLiveData<List<Workout>> by lazy { MutableLiveData<List<Workout>>() }

    init {
        updateWorkouts()
    }

    fun updateWorkouts() {
        workouts.value = workoutRepo.all().sortedBy { it.createdDate }
    }

    fun addWorkoutPlaceholder() {
        addWorkout(Workout(
            name = "Workout ${workouts.value?.count() ?: 1}",
            createdDate = Calendar.getInstance().time
        ))
    }

    fun addWorkout(workoutPlaceholder: Workout) {
        workoutRepo.save(workoutPlaceholder)
        updateWorkouts()
    }

    fun removeWorkout(workout: Workout) {
        workoutRepo.remove(workout.id)
        updateWorkouts()
    }
}