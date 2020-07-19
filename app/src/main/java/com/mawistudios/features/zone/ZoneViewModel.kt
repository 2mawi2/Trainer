package com.mawistudios.features.zone

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mawistudios.app.model.Zone
import java.util.*

class ZoneViewModel(
    private val zoneRepo: IZoneRepo
) : ViewModel() {
    val zones: MutableLiveData<List<Zone>> by lazy { MutableLiveData<List<Zone>>() }

    init {
        updateLiveData()
    }

    fun updateLiveData() {
        zones.value = zoneRepo.all().sortedBy { it.index }
    }

    fun addZonePlaceholder() {
        addZone(
            Zone(min = 0.0, max = 0.0, name = "", index = 0)
        )
    }

    fun addZone(zonePlaceholder: Zone) {
        zoneRepo.save(zonePlaceholder)
        updateLiveData()
    }

    fun removeZone(zone: Zone) {
        zoneRepo.remove(zone.id)
        updateLiveData()
    }
}