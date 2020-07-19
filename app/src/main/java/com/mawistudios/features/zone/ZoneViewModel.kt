package com.mawistudios.features.zone

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mawistudios.app.model.Zone
import com.mawistudios.data.local.IPropertyRepo
import kotlin.math.round

class ZoneViewModel(
    private val zoneRepo: IZoneRepo,
    private val propertyRepo: IPropertyRepo
) : ViewModel() {
    val zonesLiveData: MutableLiveData<List<Zone>> by lazy { MutableLiveData<List<Zone>>() }
    val ftpLiveData: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }

    init {
        updateLiveData()
    }


    fun updateLiveData() {
        val ftp = updateFtpLiveData()
        updateZonesLiveData(ftp)
    }

    private fun updateFtpLiveData(): Double {
        var ftp = propertyRepo.getDouble("ftp")
        if (ftp == null) {
            val defaultFtp = 200.0
            propertyRepo.saveDouble("ftp", defaultFtp)
            ftp = defaultFtp
        }
        ftpLiveData.value = ftp
        return ftp
    }

    private fun updateZonesLiveData(ftp: Double) {
        val persistedZones = zoneRepo.all()
        if (!persistedZones.any()) {
            val defaultPowerZones = calculatePowerZones(ftp)
            zoneRepo.save(defaultPowerZones)
        }
        zonesLiveData.value = zoneRepo.all().sortedBy { it.min }
    }

    fun recalculateZones() {
        updateFtpLiveData()
        ftpLiveData.value?.let { ftp ->
            val powerZones = calculatePowerZones(ftp)
            zoneRepo.removeAll()
            zoneRepo.save(powerZones)
            updateLiveData()
        }
    }

    private fun calculatePowerZones(ftp: Double): List<Zone> {
        return listOf(
            Zone(name = "ACTIVE RECOVERY", min = round(ftp * 0.0), max = round(ftp * 0.55)),
            Zone(name = "ENDURANCE", min = round(ftp * 0.56), max = round(ftp * 0.75)),
            Zone(name = "TEMPO", min = round(ftp * 0.76), max = round(ftp * 0.90)),
            Zone(name = "LACTATE THRESHOLD", min = round(ftp * 0.91), max = round(ftp * 1.05)),
            Zone(name = "VO2 MAX", min = round(ftp * 1.06), max = round(ftp * 1.20)),
            Zone(name = "ANAEROBIC CAPACITY", min = round(ftp * 1.21), max = round(ftp * 1.5))
        )
    }

    fun updateFtp(ftp: Double) {
        propertyRepo.saveDouble("ftp", ftp)
    }
}