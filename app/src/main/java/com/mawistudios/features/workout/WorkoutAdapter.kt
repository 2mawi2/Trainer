package com.mawistudios.features.workout

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mawistudios.app.model.Workout
import com.mawistudios.trainer.R


abstract class BaseViewHolder(itemView: View?) : ViewHolder(itemView!!) {
    var currentPosition = 0
        private set

    protected abstract fun clear()

    open fun onBind(position: Int) {
        currentPosition = position
        clear()
    }
}

class WorkoutAdapter(workouts: List<Workout>) : RecyclerView.Adapter<BaseViewHolder>() {
    private val workoutList: List<Workout> = workouts

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_workout, parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int = 0

    override fun getItemCount(): Int {
        return if (workoutList.isNotEmpty()) {
            workoutList.size
        } else {
            0
        }
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        var workoutName: TextView
        var workoutDate: TextView

        override fun clear() {
            workoutName.text = ""
            workoutDate.text = ""
        }


        override fun onBind(position: Int) {
            super.onBind(position)
            val workout: Workout = workoutList[position]

            workoutName.text = workout.name
            workoutDate.text = workout.createdDate.toString()
        }

        init {
            workoutName = itemView.findViewById(R.id.workout_name)
            workoutDate = itemView.findViewById(R.id.workout_date)
        }
    }
}