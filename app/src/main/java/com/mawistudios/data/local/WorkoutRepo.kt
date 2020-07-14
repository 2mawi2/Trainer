package com.mawistudios.data.local

import com.mawistudios.app.model.Interval
import com.mawistudios.app.model.Workout
import io.objectbox.kotlin.boxFor

interface IWorkoutRepo : IBaseRepo<Workout> {
    fun getWorkout(): Workout
    fun addInterval(workoutId: Long, interval: Interval)
    fun removeInterval(workoutId: Long, intervalId: Interval)
}

class WorkoutRepo : BaseRepo<Workout>(ObjectBox.boxStore.boxFor()), IWorkoutRepo {
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

        val intBox = ObjectBox.boxStore.boxFor<Interval>()
        val int = intBox.get(interval.id)
        intBox.attach(int)

        workout.intervals.remove(int)
        intBox.remove(int)
    }
}