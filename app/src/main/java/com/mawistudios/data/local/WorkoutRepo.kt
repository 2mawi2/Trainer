package com.mawistudios.data.local

import com.mawistudios.app.model.Workout
import io.objectbox.kotlin.boxFor
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


interface IWorkoutRepo {
    fun getWorkout(): Workout
    fun getWorkouts(): List<Workout>
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

    override fun getWorkouts(): List<Workout> {
        return listOf(
            Workout(
                name = "Workout 1",
                createdDate = Calendar.getInstance().time
            )
        )
    }
}