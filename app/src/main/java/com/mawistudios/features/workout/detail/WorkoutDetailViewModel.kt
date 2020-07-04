package com.mawistudios.features.workout.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mawistudios.app.model.Workout
import com.mawistudios.data.local.IWorkoutRepo

class WorkoutDetailViewModel(
    private val workoutRepo: IWorkoutRepo
) : ViewModel() {
    val workout: MutableLiveData<Workout> by lazy { MutableLiveData<Workout>() }

    fun setWorkout(workoutId: Long) {
        workout.value = workoutRepo.get(workoutId)
    }

    fun saveForm(formName: String) {
        val updatedWorkout = workout.value

        updatedWorkout?.let {

            updatedWorkout.name = formName

            workout.value = updatedWorkout
            workoutRepo.save(updatedWorkout)
            updateLiveData()
        }
    }

    private fun updateLiveData() {
        workout.value = workout.value?.id?.let { workoutRepo.get(it) }
    }
}