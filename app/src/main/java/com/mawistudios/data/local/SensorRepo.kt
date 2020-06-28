package com.mawistudios.data.local

import com.mawistudios.app.model.Sensor
import com.mawistudios.app.model.Sensor_
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query

interface ISensorRepo : IBaseRepo<Sensor> {
    fun addOrUpdate(sensor: Sensor)
}

class SensorRepo : BaseRepo<Sensor>(ObjectBox.boxStore.boxFor()), ISensorRepo {
    override fun addOrUpdate(sensor: Sensor) {
        val entity = box.query {
            equal(Sensor_.name, sensor.name)
        }.find().firstOrNull()

        if (entity != null) {
            update(entity.id) {
                it.state = sensor.state
                it.params = sensor.params
            }
        } else {
            add(sensor)
        }
    }
}
