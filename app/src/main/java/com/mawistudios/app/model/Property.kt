package com.mawistudios.app.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.io.Serializable

@Entity
data class Property(
    @Id var id: Long = 0,
    var identifier: String,
    var value: String?
) : Serializable