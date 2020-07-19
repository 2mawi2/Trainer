package com.mawistudios.features

import com.mawistudios.app.model.Sensor
import com.mawistudios.app.model.Sensor_
import com.mawistudios.data.local.BaseRepo
import com.mawistudios.data.local.IBaseRepo
import com.mawistudios.data.local.ObjectBox
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query

interface ISensorRepo : IBaseRepo<Sensor> {
    fun addOrUpdate(sensor: Sensor)
}

class SensorRepo : BaseRepo<Sensor>(ObjectBox.boxStore.boxFor()),
    ISensorRepo {
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
            save(sensor)
        }
    }
}
