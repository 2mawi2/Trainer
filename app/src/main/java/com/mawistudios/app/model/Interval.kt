package com.mawistudios.app.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import java.io.Serializable
import java.time.Duration

@Entity
data class Interval(
    @Id var id: Long = 0,
    var duration: Long,
    @Transient var targetCadence: Zone,
    @Transient var targetHearthRate: Zone,
    val start: Long = 0,
    val end: Long = 0,
    val workoutId: Long = 0
) : Serializable {
    var workout: ToOne<Workout> = ToOne(this, Interval_.workout)
    fun duration(): Duration = Duration.ofMillis(start - end)
}