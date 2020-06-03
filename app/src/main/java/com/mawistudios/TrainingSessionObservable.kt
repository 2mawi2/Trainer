package com.mawistudios

import com.wahoofitness.connector.conn.connections.params.ConnectionParams

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
        deviceName: String,
        state: String
    ) {
        observers.forEach { it.onSensorConnectionStateChanged(deviceName, state) }
    }
}