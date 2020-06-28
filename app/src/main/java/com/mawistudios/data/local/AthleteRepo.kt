package com.mawistudios.data.local

import com.mawistudios.app.model.Zone


interface IAthleteRepo {
    fun getUserHearthRateZones(): List<Zone>
}

class AthleteRepo : IAthleteRepo {
    override fun getUserHearthRateZones(): List<Zone> {
        val threshold = 164.0
        return listOf(
            Zone(
                0.0 * threshold,
                0.81 * threshold,
                "1 Recovery",
                index = 1
            ),
            Zone(
                0.81 * threshold,
                0.89 * threshold,
                "2 Aerobic",
                index = 2
            ),
            Zone(
                0.89 * threshold,
                0.93 * threshold,
                "3 Tempo",
                index = 3
            ),
            Zone(
                0.93 * threshold,
                0.99 * threshold,
                "4 SubThreshold",
                index = 4
            ),
            Zone(
                0.99 * threshold,
                1.02 * threshold,
                "5A SubThreshold",
                index = 5
            ),
            Zone(
                1.02 * threshold,
                1.06 * threshold,
                "5B Aerobic Capacity",
                index = 6
            ),
            Zone(
                1.06 * threshold,
                1.3 * threshold,
                "5C Anaerobic Capacity",
                index = 7
            )
        )
    }
}