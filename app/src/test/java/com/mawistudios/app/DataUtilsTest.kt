package com.mawistudios.app

import com.mawistudios.data.local.Zone
import com.mawistudios.data.local.TrainingInterval
import com.mawistudios.data.local.TrainingProgram
import org.junit.Test

import org.junit.Assert.*
import java.time.Duration

class DataUtilsTest {
    private fun getTrainingProgram(): TrainingProgram {
        val hit = TrainingInterval(
            duration = Duration.ofMinutes(10).toMillis(),
            targetCadence = Zone(70.0, 80.0),
            targetHearthRate = Zone(160.0, 190.0)
        )
        val endurance = TrainingInterval(
            duration = Duration.ofMinutes(10).toMillis(),
            targetCadence = Zone(60.0, 70.0),
            targetHearthRate = Zone(120.0, 140.0)
        )
        return TrainingProgram(
            intervals = listOf(endurance, hit, endurance)
        )
    }

    @Test
    fun `should parse training program to graph format`() {
        val trainingProgram = getTrainingProgram()
        val expected = listOf(
            listOf(Pair(0.0f, 140.0f), Pair(10.0f, 140.0f)),
            listOf(Pair(10.0f, 190.0f), Pair(20.0f, 190.0f)),
            listOf(Pair(20.0f, 140.0f), Pair(30.0f, 140.0f))
        )

        val result = trainingProgram.toGraphFormat()

        expected.zip(result).forEach {
            assertArrayEquals(it.first.toTypedArray(), it.second.toTypedArray())
        }
    }
}