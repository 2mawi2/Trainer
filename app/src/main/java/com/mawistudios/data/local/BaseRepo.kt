package com.mawistudios.data.local

import io.objectbox.Box

abstract class BaseRepo<T>(protected val box: Box<T>) {
    fun add(entity: T) = box.put(entity)
    fun last(): T = box.all.last() // TODO find more efficient solution

}