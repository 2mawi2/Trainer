package com.mawistudios.features.zone

import com.mawistudios.app.model.Zone
import com.mawistudios.data.local.BaseRepo
import com.mawistudios.data.local.IBaseRepo
import com.mawistudios.data.local.ObjectBox
import io.objectbox.kotlin.boxFor

interface IZoneRepo : IBaseRepo<Zone>
class ZoneRepo : BaseRepo<Zone>(ObjectBox.boxStore.boxFor()), IZoneRepo