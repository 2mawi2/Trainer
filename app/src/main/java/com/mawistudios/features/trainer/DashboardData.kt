package com.mawistudios.features.trainer

import com.mawistudios.app.model.SensorData

data class DashboardData(
    var hearthRate: SensorData?,
    val bpm: String?,
    val kmh: String?,
    val cadence: String?
)