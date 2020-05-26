package com.mawistudios.data.local

import org.junit.Test

import org.junit.Assert.*

class ZoneTest {

    @Test
    fun `should match hearth rate`() {
        assertTrue(Zone(0.0, 100.0).matches(0.00001))
        assertTrue(Zone(0.0, 100.0).matches(100.0))
        assertTrue(Zone(0.0, 100.0).matches(50.0))
        assertTrue(Zone(0.0, 200.0).matches(200.0))

        assertTrue(Zone(0.0, 132.84).matches(132.84))
        assertTrue(Zone(0.0, 132.84).matches(132.84))
    }

    @Test
    fun `should format to string`() {
        assertEquals("50-100", Zone(50.0, 100.0).toString())
    }
}