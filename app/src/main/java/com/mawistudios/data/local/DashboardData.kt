package com.mawistudios.data.local

import com.mawistudios.data.local.SensorData

data class DashboardData(
    var hearthRate: SensorData?,
    val bpm: String?,
    val kmh: String?,
    val cadence: String?
)