package com.mawistudios.data.local

import io.objectbox.kotlin.boxFor

object SensorRepo : BaseRepo<Sensor>(ObjectBox.boxStore.boxFor())