package com.mawistudios

import com.mawistudios.data.local.Sensor

object TrainingSessionObservable {
    private var observers = mutableListOf<ITrainingSessionObserver>()

    fun register(trainingSessionObserver: ITrainingSessionObserver) {
        observers.add(trainingSessionObserver)
    }

    fun unRegister(trainingSessionObserver: ITrainingSessionObserver) {
        observers.removeIf { it == trainingSessionObserver }
    }

    fun onTrainingDataChanged() = observers.forEach { it.onTrainingDataChanged() }

    fun onDiscoveryStarted() = observers.forEach { it.onDiscoveryStarted() }

    fun onSensorConnectionStateChanged(
        sensor: Sensor
    ) {
        observers.forEach { it.onSensorConnectionStateChanged(sensor) }
    }
}