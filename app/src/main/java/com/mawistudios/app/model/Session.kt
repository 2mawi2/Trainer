package com.mawistudios.app.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

@Entity
data class Session(
    @Id var id: Long = 0,
    var startTime: Date? = null,
    var endTime: Date? = null
) {
    fun hasStarted() = startTime != null
    fun hasEnded() = endTime != null
    fun isActive() = hasStarted() && hasEnded().not()
}