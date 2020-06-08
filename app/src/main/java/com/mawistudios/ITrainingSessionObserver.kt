package com.mawistudios

import com.mawistudios.data.local.Sensor

interface ITrainingSessionObserver {
    fun onTrainingDataChanged()
    fun onDiscoveryStarted()
    fun onSensorConnectionStateChanged(
        sensor: Sensor
    )
}