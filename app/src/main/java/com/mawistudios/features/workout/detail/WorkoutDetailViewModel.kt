package com.mawistudios.features.workout.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mawistudios.app.model.Interval
import com.mawistudios.app.model.Workout
import com.mawistudios.app.model.Zone
import com.mawistudios.features.workout.IWorkoutRepo
import kotlinx.android.synthetic.main.activity_workout_detail.*

class WorkoutDetailViewModel(
    private val workoutRepo: IWorkoutRepo
) : ViewModel() {
    val workout: MutableLiveData<Workout> by lazy { MutableLiveData<Workout>() }
    val intervals: MutableLiveData<MutableList<Interval>> by lazy { MutableLiveData<MutableList<Interval>>() }
    private var workoutId: Long? = null

    fun setWorkout(workoutId: Long) {
        this.workoutId = workoutId
        updateLiveData()
    }

    fun updateLiveData() {
        workout.value = workoutId?.let { workoutRepo.get(it) }
        intervals.value = workout.value!!.intervals
    }

    fun removeInterval(interval: Interval) {
        workoutId?.let {
            workoutRepo.removeInterval(it, interval)
            updateLiveData()
        }
    }

    fun addIntervalPlaceholder() {
        workoutId?.let {
            workoutRepo.addInterval(
                it, Interval(
                    name = "Interval ${(intervals.value?.count() ?: 0) + 1}",
                    duration = 200,
                    targetCadence = Zone(0.0, 0.0),
                    targetHearthRate = Zone(0.0, 0.0),
                    targetPower = Zone(0.0, 0.0)
                )
            )
            updateLiveData()
        }
    }

    fun addLoopPlaceholder() {
    }

    fun setWorkoutName(input: String) {
        workout.value?.let {
            it.name = input
            workout.value = it
        }
    }

    fun saveWorkout() {
        workout.value?.let { workoutRepo.save(it) }
    }
}