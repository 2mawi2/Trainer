package com.mawistudios.data.local

import io.objectbox.Box

abstract class BaseRepo<T>(protected val box: Box<T>) {
    fun add(entity: T) = box.put(entity)
    fun add(vararg entities: T) = box.put(*entities)
    fun last(): T = box.all.last() // TODO find more efficient solution
    fun update(id: Long, operation: (entity: T) -> Unit): T {
        val entity = box.get(id)
        operation(entity)
        box.put(entity)
        return box.get(id)
    }

    fun get(id: Long): T = box.get(id)
}