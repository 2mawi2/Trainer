package com.mawistudios.app

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.mawistudios.app.model.Sensor
import com.wahoofitness.connector.HardwareConnectorEnums
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import kotlin.math.abs


fun areClose(first: Double, second: Double, precision: Double): Boolean {
    val distance = abs(first - second)
    return distance <= precision
}


object GlobalState {
    var isKoinInitialized = false
    var isObjectBoxInitialized = false
    var isLocationPermissionRequested = false
}

fun appendQueryParam(uri: String, queryParam: String): URI {
    val oldUri = URI(uri)

    var newQuery = oldUri.query

    if (newQuery == null) {
        newQuery = queryParam
    } else {
        newQuery += "&$queryParam"
    }

    return URI(
        oldUri.scheme, oldUri.authority,
        oldUri.path, newQuery, oldUri.fragment
    )
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)

fun Activity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

suspend fun <T> asyncIO(block: suspend CoroutineScope.() -> T) = withContext(Dispatchers.IO) {
    block()
}

inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
    var sum = 0L
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

fun HardwareConnectorEnums.SensorConnectionState.asString(): String = when (this) {
    HardwareConnectorEnums.SensorConnectionState.DISCONNECTED -> "DISCONNECTED"
    HardwareConnectorEnums.SensorConnectionState.CONNECTING -> "CONNECTING"
    HardwareConnectorEnums.SensorConnectionState.CONNECTED -> "CONNECTED"
    HardwareConnectorEnums.SensorConnectionState.DISCONNECTING -> "DISCONNECTING"
}

fun Sensor.resetState(): Sensor {
    state = HardwareConnectorEnums.SensorConnectionState.DISCONNECTED.asString()
    return this
}