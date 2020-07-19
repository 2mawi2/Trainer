package com.mawistudios.features.workout

import com.mawistudios.features.trainer.TrainerViewModel
import com.mawistudios.features.workout.IWorkoutRepo
import com.mawistudios.features.workout.WorkoutRepo
import com.mawistudios.features.workout.WorkoutViewModel
import com.mawistudios.features.workout.detail.WorkoutDetailViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val workoutModule = module {
    single { WorkoutRepo(get()) as IWorkoutRepo }
    viewModel { TrainerViewModel(get(), get(), get(), get(), get()) }
    viewModel { WorkoutViewModel(get()) }
    viewModel { WorkoutDetailViewModel(get()) }
}