package com.mawistudios.app.model

import com.mawistudios.app.sumByLong
import java.time.Duration

data class TrainingProgram(
    var intervals: List<TrainingInterval>
) {
    fun totalDuration(): Duration {
        return Duration.ofMillis(intervals.sumByLong { it.duration })
    }
}