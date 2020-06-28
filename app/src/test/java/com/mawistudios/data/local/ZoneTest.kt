package com.mawistudios.data.local

import com.mawistudios.app.model.Zone
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@Suppress("UsePropertyAccessSyntax")
class ZoneTest {

    @ParameterizedTest
    @CsvSource(
        value = [
            "100.0, 0.00001",
            "100.0, 100.0",
            "100.0, 50.0",
            "200.0, 200.0",
            "132.84, 132.84",
            "132.84, 132.84"
        ]
    )
    fun `should match hearth rate`(max: Double, hearthRate: Double) {
        assertThat(Zone(0.0, max).matches(hearthRate)).isTrue()
    }

    @Test
    fun `should format to string`() {
        assertThat(Zone(50.0, 100.0).toString()).isEqualTo("50-100")
    }
}