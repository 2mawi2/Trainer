package com.mawistudios

import android.Manifest
import android.widget.Toast
import com.mawistudios.app.GlobalState
import com.mawistudios.app.appModule
import com.mawistudios.data.local.ObjectBox
import org.koin.core.context.startKoin
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


@AfterPermissionGranted(1)
fun MainActivity.requestLocationPermission() {
    if (!GlobalState.isLocationPermissionRequested) {
        val perms = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Please grant the location permission",
                1,
                *perms
            )
        }
        GlobalState.isLocationPermissionRequested = true
    }
}

fun MainActivity.initPersistence() {
    if (!GlobalState.isObjectBoxInitialized) {
        ObjectBox.init(this)
        GlobalState.isObjectBoxInitialized = true
    }
}

fun initKoin() {
    if (!GlobalState.isKoinInitialized) {
        startKoin {
            printLogger()
            modules(appModule)
        }
        GlobalState.isKoinInitialized = true
    }
}
