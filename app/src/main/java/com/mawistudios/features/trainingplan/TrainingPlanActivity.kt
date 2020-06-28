package com.mawistudios.features.trainingplan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mawistudios.trainer.R
import org.koin.android.ext.android.inject


class TrainingPlanActivity : AppCompatActivity() {
    private val viewModel: TrainingPlanViewModel by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_plan)
    }
}