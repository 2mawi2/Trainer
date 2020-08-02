package com.mawistudios.app.model

import com.mawistudios.app.hoursToMillis
import com.mawistudios.app.minutesToMillis
import com.mawistudios.app.secondsToMillis
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import java.io.Serializable
import java.time.Duration

@Entity
data class Interval(
    @Id var id: Long = 0,
    var seconds: Int = 0,
    var minutes: Int = 0,
    var hours: Int = 0,
    @Transient var targetCadence: Zone,
    @Transient var targetHearthRate: Zone,
    @Transient var targetPower: Zone,
    var name: String = "",
    val start: Long = 0,
    val end: Long = 0,
    val workoutId: Long = 0
) : Serializable {
    var workout: ToOne<Workout> = ToOne(this, Interval_.workout)
    fun duration(): Duration = Duration.ofMillis(
        secondsToMillis(seconds) + minutesToMillis(minutes) + hoursToMillis(hours)
    )

    fun getFormattedDuration() = "${hours}h ${minutes}min ${seconds}sec"
}