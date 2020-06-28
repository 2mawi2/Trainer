package com.mawistudios.app

import android.content.Context
import com.mawistudios.features.trainer.TrainerViewModel
import com.mawistudios.data.hardware.HardwareManager
import com.mawistudios.data.hardware.IHardwareManager
import com.mawistudios.data.hardware.sensors.ISensorManager
import com.mawistudios.data.hardware.sensors.SensorManager
import com.mawistudios.data.local.*
import com.mawistudios.features.workout.WorkoutViewModel
import org.koin.dsl.module

val appModule = module {
    single { SessionRepo() as ISessionRepo }
    single { SensorRepo() as ISensorRepo }
    single { SensorDataRepo() as ISensorDataRepo }
    single { AthleteRepo() as IAthleteRepo }
    single { WorkoutRepo() as IWorkoutRepo }
    single { Logger() as ILogger }
    factory { SensorManager(get()) as ISensorManager }

    factory { TrainerViewModel(get(), get(), get(), get(), get()) }
    factory { WorkoutViewModel(get()) }

    factory { (context: Context) -> HardwareManager(context, get()) as IHardwareManager }
}