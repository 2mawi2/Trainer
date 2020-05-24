package com.mawistudios.data.local

import android.content.Context
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.kotlin.boxFor
import java.util.*

object ObjectBox {
    lateinit var boxStore: BoxStore
        private set

    fun init(context: Context) {
        boxStore = MyObjectBox.builder().androidContext(context.applicationContext).build()
    }
}

@Entity
data class Sensor(
    @Id var id: Long = 0,
    var connectionParams: String? = null,
    var name: String
)

@Entity
data class SensorData(
    @Id var id: Long = 0,
    var time: Date,
    var dataPoint: Double
) {
    //lateinit var sensor: ToOne<Sensor>
}

abstract class BaseRepo<T>(private val box: Box<T>) {
    fun add(entity: T) = box.put(entity)
    fun last(): T = box.all.last() // TODO find more efficient solution
}

object SensorDataRepo : BaseRepo<SensorData>(ObjectBox.boxStore.boxFor())
object SensorRepo : BaseRepo<Sensor>(ObjectBox.boxStore.boxFor())