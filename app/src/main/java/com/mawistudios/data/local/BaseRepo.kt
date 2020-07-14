package com.mawistudios.data.local

import io.objectbox.Box


interface IBaseRepo<T> {
    fun save(entity: T): Long
    fun save(vararg entities: T)
    fun last(): T
    fun update(id: Long, operation: (entity: T) -> Unit): T
    fun get(id: Long): T
    fun all(): List<T>
    fun remove(id: Long): Boolean
    fun remove(entitiy: T): Boolean
}

abstract class BaseRepo<T>(protected val box: Box<T>) : IBaseRepo<T> {
    override fun save(entity: T) = box.put(entity)
    override fun save(vararg entities: T) = box.put(*entities)
    override fun last(): T = box.all.last() // TODO find more efficient solution
    override fun update(id: Long, operation: (entity: T) -> Unit): T {
        val entity = box.get(id)
        operation(entity)
        box.put(entity)
        return box.get(id)
    }

    override fun get(id: Long): T = box.get(id)
    override fun all(): List<T> = box.all
    override fun remove(id: Long): Boolean = box.remove(id)
    override fun remove(entitiy: T): Boolean = box.remove(entitiy)
}