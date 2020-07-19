package com.mawistudios.features.trainer

import com.mawistudios.features.trainer.*
import org.koin.dsl.module


val trainerModule = module {
    single { AthleteRepo() as IAthleteRepo }
    single { IntervalRepo() as IIntervalRepo }
    single { SessionRepo() as ISessionRepo }
}
