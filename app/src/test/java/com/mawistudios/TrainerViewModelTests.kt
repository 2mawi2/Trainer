package com.mawistudios

import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc
import com.mawistudios.data.local.ISensorDataRepo
import com.mawistudios.data.local.ISessionRepo
import com.mawistudios.data.local.SensorData
import com.mawistudios.data.local.Session
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test

import org.junit.Assert.*
import org.mockito.Mockito.mock
import java.util.*

class TrainerViewModelTests {
    var sessionRepoMock: ISessionRepo = mock()
    var sensorDataRepoMock: ISensorDataRepo = mock()

    fun sut(): TrainerViewModel = TrainerViewModel(
        sessionRepoMock,
        sensorDataRepoMock
    )

    @Test
    fun `should use first interval when hearthRateData is null`() {
        whenever(sessionRepoMock.get(any())).thenReturn(Session(startTime = Date()))

        sut().apply {
            val result = getCurrentInterval(null)
            assertEquals(trainingProgram.intervals.first(), result)
        }
    }

    @Test
    fun `should use first interval when startTime is null`() {
        whenever(sessionRepoMock.get(any())).thenReturn(Session(startTime = null))

        sut().apply {
            val result = getCurrentInterval(
                SensorData(
                    time = Date(),
                    dataPointType = "type",
                    dataPoint = 1.2
                )
            )
            assertEquals(trainingProgram.intervals.first(), result)
        }
    }
}