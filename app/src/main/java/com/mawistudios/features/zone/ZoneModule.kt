package com.mawistudios.features.zone

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val zoneModule = module {
    single { ZoneRepo() as IZoneRepo }
    viewModel { ZoneViewModel(get()) }
}