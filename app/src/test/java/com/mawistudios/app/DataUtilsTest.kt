package com.mawistudios.app

import com.mawistudios.data.local.Zone
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DataUtilsTest {
    @Test
    fun `should calculate correct interval progress percentage`() {
        val targetZone = Zone(min = 100.0, max = 200.0)
        val measurement = 160.0

        val result = calcIntervalProgressPercentage(targetZone, measurement)

        assertThat(result).isEqualTo(60)
    }

    @Test
    fun `should clip minimum`() {
        val targetZone = Zone(min = 130.0, max = 150.0)
        val measurement = 120.0

        val result = calcIntervalProgressPercentage(targetZone, measurement)

        assertThat(result).isEqualTo(0)
    }

    @Test
    fun `should clip maximum`() {
        val targetZone = Zone(min = 130.0, max = 150.0)
        val measurement = 160.0

        val result = calcIntervalProgressPercentage(targetZone, measurement)

        assertThat(result).isEqualTo(100)
    }
}