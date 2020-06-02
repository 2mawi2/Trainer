package com.mawistudios.data.local

import io.objectbox.kotlin.boxFor

interface ISensorRepo : IBaseRepo<Sensor>

class SensorRepo : BaseRepo<Sensor>(ObjectBox.boxStore.boxFor()), ISensorRepo {

}
