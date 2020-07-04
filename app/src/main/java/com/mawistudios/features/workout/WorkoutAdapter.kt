package com.mawistudios.features.workout

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mawistudios.app.model.Workout
import kotlinx.android.synthetic.main.list_item_workout.view.*
import java.text.SimpleDateFormat

class WorkoutAdapter(
    private val onClickRemove: (Workout) -> Unit,
    private val onClickModify: (Workout) -> Unit
) : ListAdapter<Workout, WorkoutAdapter.WorkoutViewHolder>(DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        getItem(position)?.let { holder.setupView(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val item = WorkoutItem(parent.context)
        return WorkoutViewHolder(item, onClickRemove, onClickModify)
    }

    override fun getItemViewType(position: Int): Int = 0

    class WorkoutViewHolder(
        workoutItem: WorkoutItem,
        private val onClickRemove: (Workout) -> Unit,
        private val onClickModify: (Workout) -> Unit
    ) : RecyclerView.ViewHolder(workoutItem) {
        fun setupView(workout: Workout) {
            itemView.workout_name.text = workout.name
            itemView.workout_date.text = workout.formatedCreatedDate
            itemView.remove_btn.setOnClickListener { this.onClickRemove(workout) }
            itemView.modify_btn.setOnClickListener { this.onClickModify(workout) }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Workout> =
            object : DiffUtil.ItemCallback<Workout>() {
                override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
                    return oldItem.id == newItem.id
                }

            }
    }
}