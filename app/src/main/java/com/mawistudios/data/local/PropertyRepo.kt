package com.mawistudios.data.local

import com.mawistudios.app.model.Property
import com.mawistudios.app.model.Property_
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query

interface IPropertyRepo : IBaseRepo<Property> {
    fun  getDouble(identifier: String): Double?
    fun  saveDouble(identifier: String, value: Double?)
}

class PropertyRepo : BaseRepo<Property>(ObjectBox.boxStore.boxFor()), IPropertyRepo {
    override fun  getDouble(identifier: String): Double? {
        val property = box.query { equal(Property_.identifier, identifier) }.findFirst()
        return property?.value?.toDoubleOrNull()
    }

    override fun  saveDouble(identifier: String, value: Double?) {
        val existingProperty = box.query { equal(Property_.identifier, identifier) }.findFirst()
        if (existingProperty != null) {
            existingProperty.value = value.toString()
            save(existingProperty)
        } else {
            val newProperty = Property(identifier = identifier, value = value.toString())
            save(newProperty)
        }
    }
}