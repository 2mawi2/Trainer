package com.mawistudios.app.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.io.Serializable
import java.time.Duration

@Entity
data class Interval(
    @Id var id: Long = 0,
    var duration: Long,
    @Transient var targetCadence: Zone,
    @Transient var targetHearthRate: Zone,
    val start: Long = 0,
    val end: Long = 0
) : Serializable {
    fun duration(): Duration = Duration.ofMillis(start - end)
}