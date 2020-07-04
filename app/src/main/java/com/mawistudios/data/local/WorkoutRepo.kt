package com.mawistudios.data.local

import com.mawistudios.app.model.Sensor
import com.mawistudios.app.model.Workout
import com.mawistudios.app.model.Workout_
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query


interface IWorkoutRepo: IBaseRepo<Workout> {
    fun getWorkout(): Workout
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
}