package com.mawistudios.features.workout

import com.mawistudios.app.model.Interval
import com.mawistudios.app.model.Workout
import com.mawistudios.data.local.BaseRepo
import com.mawistudios.data.local.IBaseRepo
import com.mawistudios.features.trainer.IIntervalRepo
import com.mawistudios.data.local.ObjectBox
import io.objectbox.kotlin.boxFor

interface IWorkoutRepo : IBaseRepo<Workout> {
    fun getWorkout(): Workout
    fun addInterval(workoutId: Long, interval: Interval)
    fun removeInterval(workoutId: Long, intervalId: Interval)
}

class WorkoutRepo(
    private val intervalRepo: IIntervalRepo
) : BaseRepo<Workout>(ObjectBox.boxStore.boxFor()),
    IWorkoutRepo {
    override fun getWorkout(): Workout {
        TODO()
        //val targetCadence = Zone(70.0, 80.0)
//
        //var intervalStart: Long = 0
        //val hearthRateZones = getUserHearthRateZones()
        //val intervals =
        //    (hearthRateZones.take(6) + hearthRateZones.take(6).reversed()).toList().map {
        //        val duration = Duration.ofSeconds(60).toMillis()
        //        val intervalEnd = intervalStart + duration
        //        val interval = TrainingInterval(
        //            start = intervalStart,
        //            end = intervalEnd,
        //            duration = duration,
        //            targetCadence = targetCadence,
        //            targetHearthRate = it
        //        )
        //        intervalStart = intervalEnd
        //        interval
        //    }
        //intervals.forEach { log(it.toString()) }
        //return TrainingPlan(intervals = intervals)
    }

    override fun addInterval(workoutId: Long, interval: Interval) {
        interval.id = 0
        val workout = box.get(workoutId)
        box.attach(workout)
        workout.intervals.add(interval)
        workout.intervals.applyChangesToDb()
    }

    override fun removeInterval(workoutId: Long, interval: Interval) {
        val workout = box.get(workoutId)
        box.attach(workout)
        workout.intervals.remove(interval)
        intervalRepo.remove(interval)
    }
}