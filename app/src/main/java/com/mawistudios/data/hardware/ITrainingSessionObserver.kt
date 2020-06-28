package com.mawistudios.data.hardware

import com.mawistudios.app.model.Sensor

interface ITrainingSessionObserver {
    fun onTrainingDataChanged()
    fun onDiscoveryStarted()
    fun onSensorConnectionStateChanged(
        sensor: Sensor
    )
}