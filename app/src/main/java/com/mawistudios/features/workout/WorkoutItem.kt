package com.mawistudios.features.workout

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.mawistudios.trainer.R

class WorkoutItem(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.list_item_workout, this, true)
    }
}