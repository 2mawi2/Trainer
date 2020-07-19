package com.mawistudios.app

import android.content.Context
import com.mawistudios.data.hardware.HardwareManager
import com.mawistudios.data.hardware.IHardwareManager
import com.mawistudios.data.hardware.sensors.ISensorManager
import com.mawistudios.data.hardware.sensors.SensorManager
import com.mawistudios.data.local.ISensorDataRepo
import com.mawistudios.data.local.SensorDataRepo
import com.mawistudios.features.ISensorRepo
import com.mawistudios.features.SensorRepo
import org.koin.dsl.module

val appModule = module {
    single { SensorRepo() as ISensorRepo }
    single { SensorDataRepo() as ISensorDataRepo }
    single { Logger() as ILogger }
    factory { SensorManager(get()) as ISensorManager }
    factory { (context: Context) -> HardwareManager(context, get()) as IHardwareManager }
}

