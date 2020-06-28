package com.mawistudios.app.model

import com.mawistudios.app.areClose

data class Zone(
    var min: Double,
    var max: Double,
    var name: String = "",
    var index: Int = 0
) {
    val delta: Double = Math.abs(max - min)

    fun matches(hearthRate: Double): Boolean {
        val isMin = areClose(
            hearthRate,
            min,
            precision = 0.0001
        )
        val isMax = areClose(
            hearthRate,
            max,
            precision = 0.0001
        )

        if (hearthRate == 0.0 && isMin) {
            return true
        }
        return hearthRate > min && (hearthRate < max || isMax)
    }

    override fun toString(): String {
        return "${min.toInt()}-${max.toInt()}"
    }
}