package com.mawistudios

import com.mawistudios.data.local.*
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test

import java.util.*

class TrainerViewModelTest {
    val sessionRepoMock = mock<ISessionRepo>()
    val sensorDataRepoMock = mock<ISensorDataRepo>()
    val athleteRepoMock = mock<IAthleteRepo>()

    private fun createTrainerViewModel() = TrainerViewModel(
        sessionRepoMock,
        athleteRepoMock,
        sensorDataRepoMock
    )

    private fun setup(): Date {
        val now = System.currentTimeMillis()
        val currentTime = Date(now + 15000)
        val startTime = Date(now)
        val endTime = Date(now + 20000)
        val session = Session(
            id = 1,
            startTime = startTime,
            endTime = endTime
        )
        val trainingProgram = TrainingProgram(
            listOf(
                TrainingInterval(10000, Zone(70.0, 90.0), Zone(70.0, 90.0), 0, 10000),
                TrainingInterval(10000, Zone(70.0, 90.0), Zone(90.0, 130.0), 10000, 20000)
            )
        )
        whenever(athleteRepoMock.getTrainingProgram()).thenReturn(trainingProgram)
        whenever(sessionRepoMock.get(any())).thenReturn(session)
        return currentTime
    }

    @Test
    fun `should find current interval`() {
        val currentTime = setup()

        val interval = createTrainerViewModel().currentInterval(currentTime)

        Assertions.assertThat(interval.targetHearthRate.max).isEqualTo(130.0)
    }

    @Test
    fun `should use first interval when currentTime is not defined`() {
        setup()

        val interval = createTrainerViewModel().currentInterval(null)

        Assertions.assertThat(interval.targetHearthRate.max).isEqualTo(90.0)
    }
}