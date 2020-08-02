package com.mawistudios.features.zone

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mawistudios.app.model.Zone
import com.mawistudios.data.local.IPropertyRepo

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
        val difference = 0.0000001
        return listOf(
            Zone(name = "ACTIVE RECOVERY", min = ftp * 0.0, max = ftp * 0.56 - difference),
            Zone(name = "ENDURANCE", min = ftp * 0.56, max = ftp * 0.76 - difference),
            Zone(name = "TEMPO", min = ftp * 0.76, max = ftp * 0.91 - difference),
            Zone(name = "LACTATE THRESHOLD", min = ftp * 0.91, max = ftp * 1.06 - difference),
            Zone(name = "VO2 MAX", min = ftp * 1.06, max = ftp * 1.21 - difference),
            Zone(name = "ANAEROBIC CAPACITY", min = ftp * 1.21, max = ftp * 1.5)
        )
    }

    fun updateFtp(ftp: Double) {
        propertyRepo.saveDouble("ftp", ftp)
    }
}