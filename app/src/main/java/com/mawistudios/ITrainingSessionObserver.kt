package com.mawistudios

interface ITrainingSessionObserver {
    fun onTrainingDataChanged()
    fun onDiscoveryStarted()
    fun onSensorConnectionStateChanged(
        deviceName: String,
        state: String
    )
}