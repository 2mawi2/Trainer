package com.mawistudios

import com.mawistudios.app.ILogger
import com.mawistudios.data.local.*
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Duration

import java.util.*

class TrainerViewModelTest {
    val sessionRepoMock = mock<ISessionRepo>()
    val sensorDataRepoMock = mock<ISensorDataRepo>()
    val athleteRepoMock = mock<IAthleteRepo>()
    val loggerMock = mock<ILogger>()

    private fun createTrainerViewModel() = TrainerViewModel(
        loggerMock,
        sessionRepoMock,
        athleteRepoMock,
        sensorDataRepoMock
    )

    private fun setup(
    ): Date {
        val now = System.currentTimeMillis()
        val currentTime = Date(now + 15000)
        val startTime = Date(now)
        val endTime = Date(now + 20000)
        val mockedSession = Session(
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
        whenever(sessionRepoMock.get(any())).thenReturn(mockedSession)
        return currentTime
    }

    @Test
    fun `should reuse session if active exists`() {
        val activeSession = Session(
            id = 777,
            startTime = Date(System.currentTimeMillis()),
            endTime = null
        )
        whenever(sessionRepoMock.getLastActiveOrNull()).thenReturn(activeSession)

        val viewModel = createTrainerViewModel()

        assertThat(viewModel.session).isEqualTo(activeSession)
    }

    @Test
    fun `should create new session if no active exists`() {
        val newSession = Session()
        whenever(sessionRepoMock.get(any())).thenReturn(newSession)
        whenever(sessionRepoMock.getLastActiveOrNull()).thenReturn(null)

        val viewModel = createTrainerViewModel()

        assertThat(viewModel.session).isEqualTo(newSession)
    }

    @Test
    fun `should update current hearth rate progress`() {
        val currentTime = setup()

        val interval = createTrainerViewModel().currentInterval(currentTime)

        assertThat(interval.targetHearthRate.max).isEqualTo(130.0)
    }

    @Test
    fun `should use first interval when currentTime is not defined`() {
        setup()

        val interval = createTrainerViewModel().currentInterval(null)

        assertThat(interval.targetHearthRate.max).isEqualTo(90.0)
    }

    @Test
    fun `should create new session on init`() {
        setup()

        createTrainerViewModel()

        verify(sessionRepoMock).add(any())
    }

    @Test
    fun `should calculate current duration`() {
        val currentTime = setup()

        val currentDuration = createTrainerViewModel().currentDuration(currentTime)

        assertThat(currentDuration).isEqualTo(Duration.ofMillis(15000))
    }
}