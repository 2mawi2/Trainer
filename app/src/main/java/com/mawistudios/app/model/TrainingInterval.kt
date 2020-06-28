package com.mawistudios.app.model

import java.time.Duration

data class TrainingInterval(
    var duration: Long,
    var targetCadence: Zone,
    var targetHearthRate: Zone,
    val start: Long = 0,
    val end: Long = 0
) {
    fun duration(): Duration =
        Duration.ofMillis(start - end)
}