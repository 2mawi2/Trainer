package com.mawistudios.app.model

import androidx.room.Ignore
import com.mawistudios.app.sumByLong
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.time.Duration
import java.util.*

@Entity
data class Workout(
    @Id var id: Long = 0,
    @Transient var intervals: List<TrainingInterval> = listOf(),
    var name: String,
    var createdDate: Date
) {
    fun totalDuration(): Duration = Duration.ofMillis(intervals.sumByLong { it.duration })
}