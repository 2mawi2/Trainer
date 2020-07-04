package com.mawistudios.app.model

import com.mawistudios.app.sumByLong
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*

@Entity
data class Workout(
    @Id var id: Long = 0,
    var name: String,
    var createdDate: Date
) : Serializable {
    lateinit var intervals: ToMany<Interval>
    fun totalDuration(): Duration = Duration.ofMillis(intervals?.sumByLong { it.duration } ?: 0)
    val formatedCreatedDate: String
        get() = SimpleDateFormat("dd/MM/yyyy").format(createdDate)
}
